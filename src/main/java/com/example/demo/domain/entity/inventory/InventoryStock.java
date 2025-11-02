package com.example.demo.domain.entity.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock {
    private Long id;
    private String internalCode;
    private Integer stock;
}
