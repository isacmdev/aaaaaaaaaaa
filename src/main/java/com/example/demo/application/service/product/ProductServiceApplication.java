package com.example.demo.application.service.product;

import com.example.demo.application.ex.MissingRequiredException;
import com.example.demo.application.ex.UseCaseException;
import com.example.demo.application.ex.errormessage.product.ProductErrorMessage;
import com.example.demo.application.ex.validator.ProductValidator;
import com.example.demo.application.fallbacks.ProductServiceFallbacks;
import com.example.demo.domain.entity.product.Page;
import com.example.demo.domain.entity.product.Products;
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

    @Override
//    @CircuitBreaker(name = "productService", fallbackMethod = "createProductFallback")
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
//    @CircuitBreaker(name = "productService", fallbackMethod = "getAllProductsFallback")
    public Flux<Products> getAll() {
        return productsInterfacePortOut.findAll()
                .onErrorMap(e -> new UseCaseException(ProductErrorMessage.FIND_ALL_ERROR));
    }

    @Override
//    @CircuitBreaker(name = "productService", fallbackMethod = "getByIdFallback")
    public Mono<Products> getById(Long id) {
        return productsCachePortOut.getById(id)
                .switchIfEmpty(Mono.defer(() ->
                        productsInterfacePortOut.findById(id)
                                .flatMap(productFromDb ->
                                        productFromDb != null
                                                ? productsCachePortOut.putById(productFromDb).thenReturn(productFromDb)
                                                : Mono.empty()
                                )
                ))
                .switchIfEmpty(Mono.error(new UseCaseException(ProductErrorMessage.FIND_BY_ID_ERROR)));
    }

    @Override
//    @CircuitBreaker(name = "productService", fallbackMethod = "updateProductFallback")
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
//    @CircuitBreaker(name = "productService", fallbackMethod = "deleteProductFallback")
    public Mono<Void> delete(Long id) {
        return productsInterfacePortOut.delete(id)
                .doOnSuccess(v -> productsCachePortOut.evictById(id).subscribe())
                .onErrorMap(e -> new UseCaseException(ProductErrorMessage.DELETE_ERROR));
    }

    @Override
//    @CircuitBreaker(name = "productService", fallbackMethod = "addStockFallback")
    public Mono<Products> addStock(Long id, Integer quantity) {
        if (quantity <= 0) {
            return Mono.error(new IllegalArgumentException("La cantidad debe ser mayor a cero"));
        }

        return productsInterfacePortOut.findById(id)
                .switchIfEmpty(Mono.error(new UseCaseException(ProductErrorMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    product.setStock(product.getStock() + quantity);
                    return productsInterfacePortOut.update(id, product)
                            .doOnSuccess(updatedProduct -> {
                                productsCachePortOut.evictById(id).subscribe();
                                productsCachePortOut.putById(updatedProduct).subscribe();
                            });
                })
                .onErrorResume(e -> Mono.error(new UseCaseException(ProductErrorMessage.ADD_STOCK_ERROR)));
    }

    @Override
//    @CircuitBreaker(name = "productService", fallbackMethod = "removeStockFallback")
    public Mono<Products> removeStock(Long id, Integer quantity) {
        if (quantity <= 0) {
            return Mono.error(new IllegalArgumentException("La cantidad debe ser mayor a cero"));
        }

        return productsInterfacePortOut.findById(id)
                .switchIfEmpty(Mono.error(new UseCaseException(ProductErrorMessage.PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    if (product.getStock() < quantity) {
                        return Mono.error(new UseCaseException(ProductErrorMessage.INVALID_STOCK_OPERATION));
                    }
                    product.setStock(product.getStock() - quantity);
                    return productsInterfacePortOut.update(id, product)
                            .doOnSuccess(updatedProduct -> {
                                productsCachePortOut.evictById(id).subscribe();
                                productsCachePortOut.putById(updatedProduct).subscribe();
                            });
                })
                .onErrorResume(e -> Mono.error(new UseCaseException(ProductErrorMessage.REMOVE_STOCK_ERROR)));
    }

    @Override
//    @CircuitBreaker(name = "productService", fallbackMethod = "getAllPagedFallback")
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

    public Mono<Products> addStockFallback(Long id, Integer quantity, Exception e) {
        return productServiceFallbacks.addStockFallback(id, quantity, e);
    }

    public Mono<Products> removeStockFallback(Long id, Integer quantity, Exception e) {
        return productServiceFallbacks.removeStockFallback(id, quantity, e);
    }

    public Mono<Page<Products>> getAllPagedFallback(int page, int size, Exception e) {
        return productServiceFallbacks.getAllPagedFallback(page, size, e);
    }
}