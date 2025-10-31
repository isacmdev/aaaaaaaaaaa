package com.example.demo.infrastructure.routerhanlder.circuitbreaker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Tag(name = "Monitoring Router", description = "Router para endpoints de monitoreo del sistema")
public class MonitoringRouter {

    private static final String BASE_PATH = "/monitoring";

    @Bean
    @RouterOperation(
            path = BASE_PATH + "/circuit-breaker-status",
            method = RequestMethod.GET,
            operation = @Operation(
                    operationId = "Get circuit breaker status",
                    summary = "Endpoint para obtener el estado de los Circuit Breakers",
                    description = "Endpoint para monitorear el estado actual de todos los Circuit Breakers configurados en el sistema",
                    tags = {"Monitoring Router"},
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Estado de Circuit Breakers obtenido exitosamente",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(description = "Mapa con el estado de cada Circuit Breaker")
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "500",
                                    description = "Error interno del servidor",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(description = "Información del error")
                                    )
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> monitoringRoutes(MonitoringHandler monitoringHandler) {
        return RouterFunctions.route()
                .GET(BASE_PATH + "/circuit-breaker-status", monitoringHandler::getCircuitBreakerStatus)
                .build();
    }
}