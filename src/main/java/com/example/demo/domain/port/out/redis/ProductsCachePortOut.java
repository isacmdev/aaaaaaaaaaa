package com.example.demo.domain.port.out.redis;

import com.example.demo.domain.entity.product.Products;
import reactor.core.publisher.Mono;

public interface ProductsCachePortOut {
    Mono<Products> getById(Long id);
    Mono<Void> putById(Products product);
    Mono<Void> evictById(Long id);
}

