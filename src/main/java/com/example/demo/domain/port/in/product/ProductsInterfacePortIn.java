package com.example.demo.domain.port.in.product;

import com.example.demo.domain.entity.product.Page;
import com.example.demo.domain.entity.product.Products;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsInterfacePortIn {
    Mono<Products> create(Products products);
    Flux<Products> getAll();
    Mono<Products> getById(Long id);
    Mono<Products> update(Long id, Products products);
    Mono<Void> delete(Long id);
    Mono<Products> addStock(Long id, Integer quantity);
    Mono<Products> removeStock(Long id, Integer quantity);

    Mono<Page<Products>> getAllPaged(int page, int size);
}
