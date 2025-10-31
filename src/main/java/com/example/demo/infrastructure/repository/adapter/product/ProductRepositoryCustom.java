package com.example.demo.infrastructure.repository.adapter.product;

import com.example.demo.domain.entity.product.Products;
import com.example.demo.infrastructure.repository.mapper.ProductMapperEntityDB;
import com.example.demo.infrastructure.repository.mapper.util.ProductEntityRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustom {

    private final DatabaseClient databaseClient;

    public Flux<Products> findAllPaged(int page, int size) {
        int offset = page * size;
        String sql = "SELECT * FROM products ORDER BY id LIMIT :limit OFFSET :offset";
        return databaseClient.sql(sql)
                .bind("limit", size)
                .bind("offset", offset)
                .map((row, meta) -> {
                    var entity = ProductEntityRowMapper.fromRow(row);
                    return ProductMapperEntityDB.toDomain(entity);
                })
                .all();
    }

    public Mono<Long> countAll() {
        return databaseClient.sql("SELECT COUNT(*) AS total FROM products")
                .map((row, meta) -> {
                    Number n = row.get("total", Number.class);
                    return n == null ? 0L : n.longValue();
                })
                .one();
    }
}
