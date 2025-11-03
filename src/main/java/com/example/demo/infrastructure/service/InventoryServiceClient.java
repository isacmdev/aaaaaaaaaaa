package com.example.demo.infrastructure.service;

import com.example.demo.domain.entity.inventory.InventoryStock;
import com.example.demo.domain.port.in.inventory.InventoryProductInterfacePortIn;
import com.example.demo.infrastructure.dto.response.external.InventoryStockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.example.demo.infrastructure.mapper.InventoryStockMapper;

@Component
@RequiredArgsConstructor
public class InventoryServiceClient implements InventoryProductInterfacePortIn {

    private final WebClient webClient;

    @Value("${inventory.service.url:http://localhost:8084}")
    private String inventoryServiceUrl;

    public Mono<InventoryStock> getProductByInternalCode(String internalCode) {
        return webClient.get()
                .uri("/inventory-products/internal-code/{internalCode}", internalCode)
                .retrieve()
                .bodyToMono(InventoryStockResponseDto.class)
                .map(InventoryStockMapper::toDomain)
                .onErrorResume(e -> {
                    System.err.println("Error calling inventory service: " + e.getMessage());
                    return Mono.empty();
                });
    }
}