package com.example.demo.infrastructure.repository.adapter.product;

import com.example.demo.infrastructure.repository.entity.product.ProductEntityDB;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductRepositoryR2dbc extends R2dbcRepository<ProductEntityDB, Long> {
}
