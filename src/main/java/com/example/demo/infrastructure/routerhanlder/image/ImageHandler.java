package com.example.demo.infrastructure.routerhanlder.image;

import com.example.demo.domain.port.in.image.ImageIntefacePortIn;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class ImageHandler {
    private final ImageIntefacePortIn fileService;

    public Mono<ServerResponse> uploadFiles(ServerRequest request) {
        return request.multipartData()
                .flatMapMany(parts -> {
                    List<Part> fileParts = parts.get("files");
                    if (fileParts == null) {
                        return Flux.empty();
                    }
                    return Flux.fromIterable(fileParts)
                            .filter(part -> part instanceof FilePart)
                            .cast(FilePart.class);
                })
                .as(fileService::upload)
                .collectList()
                .flatMap(files -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(files));
    }

    public Mono<ServerResponse> getFileById(ServerRequest request) {
        String id = request.pathVariable("id");

        return fileService.getImageById(id)
                .flatMap(imageFile -> {
                    Flux<DataBuffer> imageContent = fileService.getImageContent(id);
                    return ServerResponse.ok()
                            .contentType(MediaType.parseMediaType(imageFile.getContentType()))
                            .body(BodyInserters.fromDataBuffers(imageContent));
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getImageMetadata(ServerRequest request) {
        String id = request.pathVariable("id");
        return fileService.getImageById(id)
                .flatMap(image -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(image))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}