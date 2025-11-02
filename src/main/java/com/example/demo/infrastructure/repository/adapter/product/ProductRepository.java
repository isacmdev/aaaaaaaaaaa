package com.example.demo.infrastructure.repository.adapter.product;

import com.example.demo.domain.entity.product.Page;
import com.example.demo.domain.entity.product.Products;
import com.example.demo.domain.port.out.product.ProductsInterfacePortOut;
import com.example.demo.infrastructure.ex.DatabaseException;
import com.example.demo.infrastructure.ex.messageerror.ProductDatabaseErrorMessage;
import com.example.demo.infrastructure.repository.entity.product.ProductEntityDB;
import com.example.demo.infrastructure.repository.mapper.ProductMapperEntityDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@AllArgsConstructor
public class ProductRepository implements ProductsInterfacePortOut {
    private final ProductRepositoryR2dbc productRepositoryR2dbc;
    private final ProductRepositoryCustom productRepositoryCustom;

    @Override
    public Mono<Products> save(Products products) {
        LocalDateTime currentDate = LocalDateTime.now();
        products.setCreatedAt(currentDate);

        ProductEntityDB productEntityDB = ProductMapperEntityDB.toEntityDB(products);
        return productRepositoryR2dbc.save(productEntityDB)
                .map(ProductMapperEntityDB::toDomain)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.CREATE_ERROR));
    }

    @Override
    public Flux<Products> findAll() {
        return productRepositoryR2dbc.findAll()
                .map(ProductMapperEntityDB::toDomain)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.FIND_ALL_ERROR));
    }

    @Override
    public Mono<Products> findById(Long id) {
        return productRepositoryR2dbc.findById(id)
                .map(ProductMapperEntityDB::toDomain)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.FIND_BY_ID_ERROR));
    }

    @Override
    public Mono<Products> update(Long id, Products products) {
        return productRepositoryR2dbc.findById(id)
                .switchIfEmpty(Mono.error(new DatabaseException(ProductDatabaseErrorMessage.FIND_BY_ID_ERROR)))
                .flatMap(existingProduct -> {
                    ProductEntityDB updatedEntity = existingProduct.toBuilder()
                            .name(products.getName() != null ? products.getName() : existingProduct.getName())
                            .category(products.getCategory() != null ? products.getCategory() : existingProduct.getCategory())
                            .description(products.getDescription() != null ? products.getDescription() : existingProduct.getDescription())
                            .images(products.getImages() != null ? products.getImages().toString() : existingProduct.getImages())
                            .price(products.getPrice() != null ? products.getPrice() : existingProduct.getPrice())
                            .stock(products.getStock() != null ? products.getStock() : existingProduct.getStock())
                            .internalCode(products.getInternalCode() != null ? products.getInternalCode() : existingProduct.getInternalCode())
                            .createdAt(existingProduct.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return productRepositoryR2dbc.save(updatedEntity);
                })
                .map(ProductMapperEntityDB::toDomain)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.UPDATE_ERROR));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return productRepositoryR2dbc.deleteById(id)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.DELETE_ERROR));
    }

    @Override
    public Mono<Page<Products>> findAllPaged(int page, int size) {
        return productRepositoryCustom.findAllPaged(page, size)
                .collectList()
                .zipWith(productRepositoryCustom.countAll(), (list, count) -> {
                    long totalElements = count == null ? 0L : count;
                    int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
                    return Page.<Products>builder()
                            .content(list)
                            .page(page)
                            .size(size)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .build();
                })
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.FIND_ALL_ERROR));
    }

    @Override
    public Mono<Products> addStock(Long id, Integer quantity) {
        return productRepositoryR2dbc.findById(id)
                .switchIfEmpty(Mono.error(new DatabaseException(ProductDatabaseErrorMessage.FIND_BY_ID_ERROR)))
                .flatMap(product -> {
                    int newStock = product.getStock() + quantity;
                    ProductEntityDB updatedProduct = product.toBuilder()
                            .stock(newStock)
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return productRepositoryR2dbc.save(updatedProduct);
                })
                .map(ProductMapperEntityDB::toDomain)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.ADD_STOCK_ERROR));
    }

    @Override
    public Mono<Products> removeStock(Long id, Integer quantity) {
        return productRepositoryR2dbc.findById(id)
                .switchIfEmpty(Mono.error(new DatabaseException(ProductDatabaseErrorMessage.FIND_BY_ID_ERROR)))
                .flatMap(product -> {
                    if (product.getStock() < quantity) {
                        return Mono.error(new DatabaseException("No hay suficiente stock disponible"));
                    }

                    int newStock = product.getStock() - quantity;
                    ProductEntityDB updatedProduct = product.toBuilder()
                            .stock(newStock)
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return productRepositoryR2dbc.save(updatedProduct);
                })
                .map(ProductMapperEntityDB::toDomain)
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.REMOVE_STOCK_ERROR));
    }

    @Override
    public Mono<Long> countAll() {
        return productRepositoryCustom.countAll()
                .onErrorMap(e -> new DatabaseException(ProductDatabaseErrorMessage.FIND_ALL_ERROR));
    }
}
