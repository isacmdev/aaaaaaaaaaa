package com.example.demo.application.service.product;

import com.example.demo.application.ex.MissingRequiredException;
import com.example.demo.application.ex.UseCaseException;
import com.example.demo.application.ex.errormessage.product.ProductErrorMessage;
import com.example.demo.application.ex.validator.ProductValidator;
import com.example.demo.application.fallbacks.ProductServiceFallbacks;
import com.example.demo.domain.entity.product.Page;
import com.example.demo.domain.entity.product.Products;
import com.example.demo.domain.port.in.inventory.InventoryProductInterfacePortIn;
import com.example.demo.domain.port.in.product.ProductsInterfacePortIn;
import com.example.demo.domain.port.out.product.ProductsInterfacePortOut;
import com.example.demo.domain.port.out.redis.ProductsCachePortOut;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductServiceApplication implements ProductsInterfacePortIn {

    private final ProductsInterfacePortOut productsInterfacePortOut;
    private final ProductsCachePortOut productsCachePortOut;
    private final ProductValidator productValidator;
    private final ProductServiceFallbacks productServiceFallbacks;
    private final InventoryProductInterfacePortIn inventoryProductInterfacePortIn;

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "createProductFallback")
    public Mono<Products> create(Products products) {
        return Mono.fromCallable(() -> {
                    productValidator.validateCommon(products);
                    return products;
                })
                .flatMap(productsInterfacePortOut::save)
                .onErrorResume(e -> {
                    if (e instanceof MissingRequiredException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new UseCaseException(ProductErrorMessage.CREATE_ERROR));
                });
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "getAllProductsFallback")
    public Flux<Products> getAll() {
        return productsInterfacePortOut.findAll()
                .onErrorMap(e -> new UseCaseException(ProductErrorMessage.FIND_ALL_ERROR));
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "getByIdFallback")
    public Mono<Products> getById(Long id) {
        return productsCachePortOut.getById(id)
                .switchIfEmpty(Mono.defer(() ->
                        productsInterfacePortOut.findById(id)
                                .flatMap(productFromDb -> {
                                    if (productFromDb != null) {
                                        return syncStockWithInventory(productFromDb)
                                                .flatMap(updatedProduct ->
                                                        productsCachePortOut.putById(updatedProduct).thenReturn(updatedProduct)
                                                );
                                    }
                                    return Mono.empty();
                                })
                ))
                .switchIfEmpty(Mono.error(new UseCaseException(ProductErrorMessage.FIND_BY_ID_ERROR)));
    }

    private Mono<Products> syncStockWithInventory(Products product) {
        if (product.getInternalCode() == null) {
            return Mono.just(product);
        }

        return inventoryProductInterfacePortIn.getProductByInternalCode(product.getInternalCode())
                .flatMap(inventoryProduct -> {
                    if (!inventoryProduct.getStock().equals(product.getStock())) {

                        Products updatedProduct = product.toBuilder()
                                .stock(inventoryProduct.getStock())
                                .build();

                        return productsInterfacePortOut.update(product.getId(), updatedProduct);
                    }
                    return Mono.just(product);
                })
                .defaultIfEmpty(product)
                .onErrorReturn(product);
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "updateProductFallback")
    public Mono<Products> update(Long id, Products products) {
        return Mono.just(products)
                .doOnNext(productValidator::validateCommon)
                .flatMap(p -> productsInterfacePortOut.update(id, p))
                .doOnSuccess(updatedProduct ->
                        productsCachePortOut.evictById(id).subscribe()
                )
                .onErrorResume(e -> {
                    if (e instanceof MissingRequiredException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new UseCaseException(ProductErrorMessage.UPDATE_ERROR));
                });
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "deleteProductFallback")
    public Mono<Void> delete(Long id) {
        return productsInterfacePortOut.delete(id)
                .doOnSuccess(v -> productsCachePortOut.evictById(id).subscribe())
                .onErrorMap(e -> new UseCaseException(ProductErrorMessage.DELETE_ERROR));
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "getAllPagedFallback")
    public Mono<Page<Products>> getAllPaged(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 50;

        int finalPage = page;
        int finalSize = size;

        return productsInterfacePortOut.findAllPaged(finalPage, finalSize)
                .onErrorMap(error -> new UseCaseException(ProductErrorMessage.FIND_ALL_ERROR));
    }


    public Mono<Products> createProductFallback(Products products, Exception e) {
        return productServiceFallbacks.createProductFallback(products, e);
    }

    public Flux<Products> getAllProductsFallback(Exception e) {
        return productServiceFallbacks.getAllProductsFallback(e);
    }

    public Mono<Products> getByIdFallback(Long id, Exception e) {
        return productServiceFallbacks.getByIdFallback(id, e);
    }

    public Mono<Products> updateProductFallback(Long id, Products products, Exception e) {
        return productServiceFallbacks.updateProductFallback(id, products, e);
    }

    public Mono<Void> deleteProductFallback(Long id, Exception e) {
        return productServiceFallbacks.deleteProductFallback(id, e);
    }

    public Mono<Page<Products>> getAllPagedFallback(int page, int size, Exception e) {
        return productServiceFallbacks.getAllPagedFallback(page, size, e);
    }
}