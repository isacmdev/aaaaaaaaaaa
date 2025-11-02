package com.example.demo.infrastructure.dto.response.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStockResponseDto {
    private Long id;
    private String internalCode;
    private Integer stock;
}
