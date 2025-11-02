package com.example.demo.infrastructure.mapper;

import com.example.demo.domain.entity.inventory.InventoryStock;
import com.example.demo.infrastructure.dto.response.external.InventoryStockResponseDto;

public class InventoryStockMapper {

    private InventoryStockMapper(){}

    public static InventoryStock toDomain(InventoryStockResponseDto inventoryStockResponseDto){
        if (inventoryStockResponseDto == null) return null;

        return InventoryStock.builder()
                .internalCode(inventoryStockResponseDto.getInternalCode())
                .stock(inventoryStockResponseDto.getStock())
                .build();
    }

    public static InventoryStockResponseDto toResponse(InventoryStock inventoryStock){
        if (inventoryStock == null) return null;

        return InventoryStockResponseDto.builder()
                .internalCode(inventoryStock.getInternalCode())
                .stock(inventoryStock.getStock())
                .build();
    }
}