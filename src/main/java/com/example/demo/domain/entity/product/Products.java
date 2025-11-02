package com.example.demo.domain.entity.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Products {
    private Long id;
    private String internalCode;
    private String name;
    private String category;
    private String description;
    private List<String> images;
    private Double price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
