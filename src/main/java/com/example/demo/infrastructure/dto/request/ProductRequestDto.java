package com.example.demo.infrastructure.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("internal_code")
    private String internalCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("images")
    private List<String> images;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("stock")
    private Integer stock;
}
