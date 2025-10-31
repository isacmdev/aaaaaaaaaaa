package com.example.demo.application.fallbacks;

import com.example.demo.application.ex.UseCaseException;
import com.example.demo.domain.entity.product.Page;
import com.example.demo.domain.entity.product.Products;
import com.example.demo.domain.port.out.redis.ProductsCachePortOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductServiceFallbacks {

    private final ProductsCachePortOut productsCachePortOut;

    public Mono<Products> createProductFallback(Products products, Exception e) {
        return Mono.error(new UseCaseException("Servicio no disponible. No se pudo crear el producto."));
    }

    public Flux<Products> getAllProductsFallback(Exception e) {
        return Flux.error(new UseCaseException("Servicio no disponible. No se pudieron obtener los productos."));
    }

    public Mono<Products> getByIdFallback(Long id, Exception e) {
        return productsCachePortOut.getById(id)
                .switchIfEmpty(Mono.error(new UseCaseException("Servicio no disponible. Intente más tarde.")));
    }

    public Mono<Products> updateProductFallback(Long id, Products products, Exception e) {
        return Mono.error(new UseCaseException("Servicio no disponible. No se pudo actualizar el producto."));
    }

    public Mono<Void> deleteProductFallback(Long id, Exception e) {
        return Mono.error(new UseCaseException("Servicio no disponible. No se pudo eliminar el producto."));
    }

    public Mono<Products> addStockFallback(Long id, Integer quantity, Exception e) {
        return Mono.error(new UseCaseException("Servicio no disponible. No se pudo agregar stock."));
    }

    public Mono<Products> removeStockFallback(Long id, Integer quantity, Exception e) {
        return Mono.error(new UseCaseException("Servicio no disponible. No se pudo remover stock."));
    }

    public Mono<Page<Products>> getAllPagedFallback(int page, int size, Exception e) {
        return Mono.error(new UseCaseException("Servicio no disponible. No se pudieron obtener los productos paginados."));
    }
}