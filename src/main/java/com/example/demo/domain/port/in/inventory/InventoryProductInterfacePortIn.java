package com.example.demo.domain.port.in.inventory;

import com.example.demo.domain.entity.inventory.InventoryStock;
import reactor.core.publisher.Mono;

public interface InventoryProductInterfacePortIn {
    Mono<InventoryStock> getProductByInternalCode(String internalCode);
}
