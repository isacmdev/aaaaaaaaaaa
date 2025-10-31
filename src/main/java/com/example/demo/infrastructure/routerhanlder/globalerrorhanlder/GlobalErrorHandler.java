package com.example.demo.infrastructure.routerhanlder.globalerrorhanlder;

import com.example.demo.application.ex.MissingRequiredException;
import com.example.demo.application.ex.UseCaseException;
import com.example.demo.infrastructure.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable error) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return Mono.error(error);
        }

        HttpStatus status;
        String message;

        if (error instanceof MissingRequiredException) {
            status = HttpStatus.BAD_REQUEST;
            message = error.getMessage();
        } else if (error instanceof UseCaseException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = error.getMessage() != null
                    ? error.getMessage()
                    : "Error al procesar la operación solicitada.";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Error interno del servidor.";
        }

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(response);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(jsonBytes)));
        } catch (Exception e) {
            byte[] fallback = "{\"error\":\"Error interno del servidor\"}".getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(fallback)));
        }
    }
}
