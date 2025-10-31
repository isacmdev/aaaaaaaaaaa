package com.example.demo.domain.port.in.image;

import com.example.demo.domain.entity.image.ImageFile;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ImageIntefacePortIn {
    Flux<ImageFile> upload(Flux<FilePart> imageFiles);
    Flux<ImageFile> getAllImages(List<String> imageIds);
    Mono<ImageFile> getImageById(String imageId);
    Flux<DataBuffer> getImageContent(String imageId);
    Mono<Void> delete(String imageId);
}