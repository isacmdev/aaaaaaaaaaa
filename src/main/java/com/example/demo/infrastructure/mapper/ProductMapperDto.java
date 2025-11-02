package com.example.demo.infrastructure.mapper;

import com.example.demo.domain.entity.product.Products;
import com.example.demo.infrastructure.dto.request.ProductRequestDto;
import com.example.demo.infrastructure.dto.response.ProductResponseDto;

import java.util.List;

public class ProductMapperDto {
    private ProductMapperDto() {
    }

    public static Products toDomain(ProductRequestDto request) {
        if (request == null) {
            return null;
        }

        return Products.builder()
                .name(request.getName())
                .category(request.getCategory())
                .internalCode(request.getInternalCode())
                .description(request.getDescription())
                .images(request.getImages())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }

    public static ProductResponseDto toResponse(Products domain) {
        if (domain == null) {
            return null;
        }

        return ProductResponseDto.builder()
                .id(domain.getId())
                .internalCode(domain.getInternalCode())
                .name(domain.getName())
                .category(domain.getCategory())
                .description(domain.getDescription())
                .images(domain.getImages())
                .price(domain.getPrice())
                .stock(domain.getStock())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public static List<ProductResponseDto> toResponseList(List<Products> domainList) {
        if (domainList == null) {
            return List.of();
        }

        return domainList.stream()
                .map(ProductMapperDto::toResponse)
                .toList();
    }
}
