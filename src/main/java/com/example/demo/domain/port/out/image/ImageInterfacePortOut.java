package com.example.demo.domain.port.out.image;

import com.example.demo.domain.entity.image.ImageFile;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ImageInterfacePortOut {
    Flux<ImageFile> save(Flux<FilePart> imageFiles);
    Flux<ImageFile> findAllImages(List<String> imageIds);
    Mono<ImageFile> findImageById(String imageId);
    Flux<DataBuffer> getImageContent(String imageId);
    Mono<Void> delete(String imageId);
}