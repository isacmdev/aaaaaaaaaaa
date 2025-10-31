package com.example.demo.application.service.image;

import com.example.demo.application.ex.UseCaseException;
import com.example.demo.application.ex.errormessage.image.ImageErrorMessage;
import com.example.demo.domain.entity.image.ImageFile;
import com.example.demo.domain.port.in.image.ImageIntefacePortIn;
import com.example.demo.domain.port.out.image.ImageInterfacePortOut;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class ImageFileService implements ImageIntefacePortIn {
    private final ImageInterfacePortOut imageInterfacePortOut;

    @Override
    public Flux<ImageFile> upload(Flux<FilePart> imageFiles) {
        return imageInterfacePortOut.save(imageFiles)
                .onErrorMap( e -> new UseCaseException(ImageErrorMessage.UPLOAD_ERROR));
    }

    @Override
    public Flux<ImageFile> getAllImages(List<String> imageIds) {
        return imageInterfacePortOut.findAllImages(imageIds)
                .onErrorMap(e -> new UseCaseException(ImageErrorMessage.FIND_ALL_ERROR));
    }

    @Override
    public Mono<ImageFile> getImageById(String imageId) {
        return imageInterfacePortOut.findImageById(imageId)
                .onErrorMap(e -> new UseCaseException(ImageErrorMessage.FIND_BY_ID_ERROR));
    }

    @Override
    public Flux<DataBuffer> getImageContent(String imageId) {
        return imageInterfacePortOut.getImageContent(imageId)
                .onErrorMap(e -> new UseCaseException(ImageErrorMessage.GET_CONTENT_ERROR));
    }

    @Override
    public Mono<Void> delete(String imageId) {
        return imageInterfacePortOut.delete(imageId)
                .onErrorMap(e -> new UseCaseException(ImageErrorMessage.DELETE_ERROR));
    }
}
