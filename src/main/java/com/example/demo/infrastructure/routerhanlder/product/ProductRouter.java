package com.example.demo.infrastructure.routerhanlder.product;

import com.example.demo.infrastructure.dto.error.ErrorResponse;
import com.example.demo.infrastructure.dto.request.StockUpdateRequestDto;
import com.example.demo.infrastructure.dto.response.ProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Tag(name = "Product Router", description = "Router para endpoints relacionados con productos")
public class ProductRouter {

    private static final String BASE_PATH = "/products";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = BASE_PATH + "/create",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "Create a new product",
                            summary = "Endpoint para crear producto",
                            description = "Endpoint para crear un nuevo producto en el sistema",
                            tags = {"Product Router"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ProductResponseDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Producto creado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = BASE_PATH + "/paged",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "Get all products paged",
                            summary = "Endpoint para obtener todos los productos",
                            description = "Endpoint para obtener la lista de todos los productos en el sistema",

                            tags = {"Product Router"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ProductResponseDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Productos encontrados",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponseDto.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = BASE_PATH + "/{id}",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "Get product by ID",
                            summary = "Endpoint para obtener un producto por ID",
                            description = "Endpoint para obtener un producto específico utilizando su ID",
                            tags = {"Product Router"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ProductResponseDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Producto encontrado",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponseDto.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Producto NO encontrado",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = BASE_PATH + "/update/{id}",
                    method = RequestMethod.PUT,
                    operation = @Operation(
                            operationId = "Update product by ID",
                            summary = "Endpoint para actualizar un producto por ID",
                            description = "Endpoint para actualizar un producto específico utilizando su ID",
                            tags = {"Product Router"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ProductResponseDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Producto actualizado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponseDto.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Producto NO encontrado",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = BASE_PATH + "/delete/{id}",
                    method = RequestMethod.DELETE,
                    operation = @Operation(
                            operationId = "Delete product by ID",
                            summary = "Endpoint para eliminar un producto por ID",
                            description = "Endpoint para eliminar un producto específico utilizando su ID",

                            tags = {"Product Router"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ProductResponseDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Producto eliminado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponseDto.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Producto NO encontrado",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class, type = "array")
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler productHandler) {
        return RouterFunctions.route()
                .POST(BASE_PATH + "/create", productHandler::createProduct)
                .GET(BASE_PATH + "/paged", productHandler::getAllProductsPaged)
                .GET(BASE_PATH + "/{id}", productHandler::getProductById)
                .PUT(BASE_PATH + "/update/{id}", productHandler::updateProduct)
                .DELETE(BASE_PATH + "/delete/{id}", productHandler::deleteProduct)
                .build();
    }
}