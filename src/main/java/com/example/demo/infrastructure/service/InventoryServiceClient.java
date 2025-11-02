package com.example.demo.infrastructure.service;

import com.example.demo.domain.entity.inventory.InventoryStock;
import com.example.demo.domain.port.in.inventory.InventoryProductInterfacePortIn;
import com.example.demo.infrastructure.dto.response.external.InventoryStockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.example.demo.infrastructure.mapper.InventoryStockMapper;

@Component
@RequiredArgsConstructor
public class InventoryServiceClient implements InventoryProductInterfacePortIn {

    private final WebClient webClient;

    public Mono<InventoryStock> getProductByInternalCode(String internalCode) {
        return webClient.get()
                .uri("http://localhost:8084/inventory-products/internal-code/{internalCode}", internalCode)
                .retrieve()
                .bodyToMono(InventoryStockResponseDto.class)
                .map(InventoryStockMapper::toDomain)
                .onErrorResume(e -> {
                    return Mono.empty();
                });
    }
}