package com.example.demo.infrastructure.repository.mapper;

import com.example.demo.domain.entity.product.Products;
import com.example.demo.infrastructure.repository.entity.product.ProductEntityDB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ProductMapperEntityDB {
    private ProductMapperEntityDB() {}
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ProductEntityDB toEntityDB(Products product) {
        String imagesJson = "[]";
        try {
            if (product.getImages() != null) {
                imagesJson = mapper.writeValueAsString(product.getImages());
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing images list", e);
        }

        return ProductEntityDB.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(imagesJson)
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static Products toDomain(ProductEntityDB entity) {
        List<String> imagesList;
        try {
            if (entity.getImages() != null) {
                imagesList = mapper.readValue(entity.getImages(),
                        mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } else {
                imagesList = Collections.emptyList();
            }
        } catch (IOException e) {
            log.error("Error deserializing images JSON", e);
            imagesList = Collections.emptyList();
        }

        return Products.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .images(imagesList)
                .price(entity.getPrice())
                .stock(entity.getStock())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
