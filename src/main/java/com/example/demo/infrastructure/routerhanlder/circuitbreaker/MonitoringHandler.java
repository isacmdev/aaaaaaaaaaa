package com.example.demo.infrastructure.routerhanlder.circuitbreaker;

import com.example.demo.infrastructure.dto.error.ErrorResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MonitoringHandler {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @SuppressWarnings("unchecked")
    public Mono<ServerResponse> getCircuitBreakerStatus(ServerRequest request) {
        return Mono.fromCallable(() -> {
                    Map<String, Object> status = new HashMap<>();

                    circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
                        Map<String, Object> cbInfo = new HashMap<>();
                        cbInfo.put("state", cb.getState().name());
                        cbInfo.put("failureRate", cb.getMetrics().getFailureRate() + "%");
                        cbInfo.put("totalCalls", cb.getMetrics().getNumberOfBufferedCalls());
                        cbInfo.put("failedCalls", cb.getMetrics().getNumberOfFailedCalls());
                        cbInfo.put("successfulCalls", cb.getMetrics().getNumberOfSuccessfulCalls());

                        status.put(cb.getName(), cbInfo);
                    });

                    return status;
                })
                .flatMap(status ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(status)
                )
                .onErrorResume(e -> {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .message("Circuit Breaker no disponible")
                            .error("Verifica que las dependencias de Resilience4j estén configuradas")
                            .timestamp(java.time.LocalDateTime.now())
                            .build();

                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorResponse);
                });
    }
}