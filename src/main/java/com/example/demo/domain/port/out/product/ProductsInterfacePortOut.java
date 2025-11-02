package com.example.demo.domain.port.out.product;

import com.example.demo.domain.entity.product.Page;
import com.example.demo.domain.entity.product.Products;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsInterfacePortOut {
    Mono<Products> save(Products products);
    Flux<Products> findAll();
    Mono<Products> findById(Long id);
    Mono<Products> update(Long id, Products products);
    Mono<Void> delete(Long id);
    Mono<Page<Products>> findAllPaged(int page, int size);
    Mono<Long> countAll();
}
