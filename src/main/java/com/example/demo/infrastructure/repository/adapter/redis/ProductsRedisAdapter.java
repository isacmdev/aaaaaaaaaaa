package com.example.demo.infrastructure.repository.adapter.redis;

import com.example.demo.domain.entity.product.Products;
import com.example.demo.domain.port.out.redis.ProductsCachePortOut;
import com.example.demo.infrastructure.ex.InfrastructureException;
import com.example.demo.infrastructure.ex.messageerror.RedisMessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ProductsRedisAdapter implements ProductsCachePortOut {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.ttl.products:PT5M}")
    private Duration ttl;

    private String productKey(Long id) { return "product:" + id; }

    @Override
    public Mono<Products> getById(Long id) {
        return redisTemplate.opsForValue()
                .get(productKey(id))
                .map(obj -> (Products) obj)
                .doOnError(e -> new InfrastructureException(RedisMessageError.CACHE_GET_ERROR + e.getMessage()));
    }

    @Override
    public Mono<Void> putById(Products product) {
        if (product == null || product.getId() == null) {
            return Mono.empty();
        }

        return redisTemplate.opsForValue()
                .set(productKey(product.getId()), product, ttl)
                .doOnError(e -> new InfrastructureException(RedisMessageError.CACHE_PUT_ERROR + e.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> evictById(Long id) {
        return redisTemplate.delete(productKey(id)).then();
    }
}
