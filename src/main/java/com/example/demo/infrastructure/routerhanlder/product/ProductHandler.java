package com.example.demo.infrastructure.routerhanlder.product;

import com.example.demo.domain.entity.product.Products;
import com.example.demo.domain.port.in.product.ProductsInterfacePortIn;
import com.example.demo.infrastructure.dto.request.ProductRequestDto;
import com.example.demo.infrastructure.dto.request.StockUpdateRequestDto;
import com.example.demo.infrastructure.dto.response.PaginatedResponse;
import com.example.demo.infrastructure.dto.response.ProductResponseDto;
import com.example.demo.infrastructure.mapper.ProductMapperDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class ProductHandler {
    private final ProductsInterfacePortIn productsInterfacePortIn;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(ProductRequestDto.class)
                .flatMap(dto -> {
                    Products product = ProductMapperDto.toDomain(dto);
                    return productsInterfacePortIn.create(product);
                })
                .map(ProductMapperDto::toResponse)
                .flatMap(productDto ->
                        ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(productDto)
                );
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    public Mono<ServerResponse> getAllProductsPaged(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(50);

        return productsInterfacePortIn.getAllPaged(page, size)
                .map(domainPage -> {
                    List<ProductResponseDto> content = ProductMapperDto.toResponseList(domainPage.getContent());
                    return PaginatedResponse.<ProductResponseDto>builder()
                            .content(content)
                            .page(domainPage.getPage())
                            .size(domainPage.getSize())
                            .totalElements(domainPage.getTotalElements())
                            .totalPages(domainPage.getTotalPages())
                            .build();
                })
                .flatMap(pagedDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(pagedDto)
                );
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return productsInterfacePortIn.getById(id)
                .map(ProductMapperDto::toResponse)
                .flatMap(productDto ->
                        ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(productDto)
                );
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(ProductRequestDto.class)
                .flatMap(dto -> {
                    Products product = ProductMapperDto.toDomain(dto);
                    return productsInterfacePortIn.update(id, product);
                })
                .map(ProductMapperDto::toResponse)
                .flatMap(productDto ->
                        ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(productDto)
                );
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return productsInterfacePortIn.delete(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> addStock(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(StockUpdateRequestDto.class)
                .flatMap(stockRequest ->
                        productsInterfacePortIn.addStock(id, stockRequest.getQuantity())
                )
                .map(ProductMapperDto::toResponse)
                .flatMap(productDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(productDto)
                );
    }

    public Mono<ServerResponse> removeStock(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(StockUpdateRequestDto.class)
                .flatMap(stockRequest ->
                        productsInterfacePortIn.removeStock(id, stockRequest.getQuantity())
                )
                .map(ProductMapperDto::toResponse)
                .flatMap(productDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(productDto)
                );
    }

}
