package com.example.demo.infrastructure.repository.adapter.image;

import com.example.demo.domain.entity.image.ImageFile;
import com.example.demo.domain.port.out.image.ImageInterfacePortOut;
import com.example.demo.infrastructure.repository.entity.image.ImageFileEntityMongo;
import com.example.demo.infrastructure.repository.mapper.ImageFileMapperDB;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class ImageFileAdapter implements ImageInterfacePortOut {

    private final ReactiveGridFsTemplate gridFsTemplate;
    private final ReactiveMongoTemplate mongoTemplate;
    final String bucketName;
    private final String baseUrl;

    public ImageFileAdapter(ReactiveGridFsTemplate gridFsTemplate,
                            ReactiveMongoTemplate mongoTemplate,
                            @Value("${app.images.bucket-name:images}") String bucketName,
                            @Value("${app.files.base-url}") String baseUrl) {
        this.gridFsTemplate = gridFsTemplate;
        this.mongoTemplate = mongoTemplate;
        this.bucketName = bucketName;
        this.baseUrl = baseUrl;
    }

    @Override
    public Flux<ImageFile> save(Flux<FilePart> imageFiles) {
        return imageFiles.flatMap(filePart -> {
            String filename = filePart.filename();
            String contentType = filePart.headers().getContentType() != null
                    ? Objects.requireNonNull(filePart.headers().getContentType()).toString()
                    : "application/octet-stream";

            Mono<Long> sizeMono = filePart.content()
                    .map(DataBuffer::readableByteCount)
                    .reduce(0L, Long::sum);

            return sizeMono.flatMap(size -> {
                Mono<ObjectId> storedFileId = gridFsTemplate.store(filePart.content(), filename, contentType);

                return storedFileId.flatMap(id -> {
                    String url = String.format("%s/files/%s", baseUrl, id.toHexString());

                    ImageFileEntityMongo entity = ImageFileEntityMongo.builder()
                            .id(id.toHexString())
                            .filename(filename)
                            .contentType(contentType)
                            .size(size)
                            .url(url)
                            .build();

                    return mongoTemplate.save(entity)
                            .map(ImageFileMapperDB::toDomain);
                });
            });
        });
    }

    @Override
    public Flux<ImageFile> findAllImages(List<String> imageIds) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("id").in(imageIds)
                ),
                ImageFileEntityMongo.class
        ).map(ImageFileMapperDB::toDomain);
    }

    @Override
    public Mono<ImageFile> findImageById(String imageId) {
        return mongoTemplate.findById(imageId, ImageFileEntityMongo.class)
                .map(ImageFileMapperDB::toDomain);
    }

    @Override
    public Flux<DataBuffer> getImageContent(String imageId) {
        return gridFsTemplate.findOne(
                        Query.query(
                                Criteria.where("_id").is(new ObjectId(imageId))
                        )
                ).flatMapMany(gridFsTemplate::getResource)
                .flatMap(ReactiveGridFsResource::getContent);
    }

    @Override
    public Mono<Void> delete(String imageId) {
        return gridFsTemplate.delete(
                Query.query(
                        Criteria.where("_id").is(new ObjectId(imageId))
                )
        ).then(
                mongoTemplate.remove(
                        Query.query(
                                Criteria.where("id").is(imageId)
                        ),
                        ImageFileEntityMongo.class
                ).then()
        );
    }
}
