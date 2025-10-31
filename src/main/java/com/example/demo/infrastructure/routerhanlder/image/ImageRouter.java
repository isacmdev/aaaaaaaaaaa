package com.example.demo.infrastructure.routerhanlder.image;

import com.example.demo.infrastructure.dto.error.ErrorResponse;
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
@Tag(name = "Image Router", description = "Endpoints para la gestión de archivos e imágenes")
public class ImageRouter {

    private static final String BASE_PATH = "/files";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = BASE_PATH + "/upload",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "Upload image",
                            summary = "Subir una o varias imágenes",
                            description = "Endpoint para subir imágenes al servidor y almacenarlas en MongoDB/GridFS",
                            tags = {"Image Router"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Imágenes subidas correctamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error en la carga del archivo",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
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
                    path = BASE_PATH + "/{id}",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "Get file by ID",
                            summary = "Obtener imagen por ID",
                            description = "Endpoint para recuperar una imagen almacenada usando su ID en GridFS",
                            tags = {"Image Router"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Imagen encontrada y devuelta correctamente",
                                            content = @Content(
                                                    mediaType = MediaType.IMAGE_JPEG_VALUE
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Archivo no encontrado",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
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
                    path = BASE_PATH + "/{id}/metadata",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "Get file metadata",
                            summary = "Obtener metadata de imagen",
                            description = "Endpoint para obtener la metadata (nombre, tamaño, tipo MIME, etc.) de una imagen almacenada",
                            tags = {"Image Router"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Metadata encontrada",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Archivo no encontrado",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class)
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
            )
    })
    public RouterFunction<ServerResponse> fileRoutes(ImageHandler handler) {
        return RouterFunctions.route()
                .POST(BASE_PATH + "/upload", handler::uploadFiles)
                .GET(BASE_PATH + "/{id}", handler::getFileById)
                .GET(BASE_PATH + "/{id}/metadata", handler::getImageMetadata)
                .build();
    }
}
