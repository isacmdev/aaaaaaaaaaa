package com.example.demo.infrastructure.repository.mapper;

import com.example.demo.domain.entity.product.Products;
import com.example.demo.infrastructure.repository.entity.product.ProductEntityDB;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ProductMapperEntityDB {
    private ProductMapperEntityDB() {}

    public static ProductEntityDB toEntityDB(Products product) {
        return ProductEntityDB.builder()
                .id(product.getId())
                .internalCode(product.getInternalCode())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .images(convertListToString(product.getImages()))
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static Products toDomain(ProductEntityDB entity) {
        return Products.builder()
                .id(entity.getId())
                .internalCode(entity.getInternalCode())
                .name(entity.getName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .images(convertStringToList(entity.getImages()))
                .price(entity.getPrice())
                .stock(entity.getStock())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static String convertListToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }

    private static List<String> convertStringToList(String string) {
        if (string == null || string.isBlank()) {
            return Collections.emptyList();
        }

        String cleanedString = string.replaceAll("[\\[\\]]", "").trim();

        if (cleanedString.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(cleanedString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}