package com.example.demo.infrastructure.repository.mapper;

import com.example.demo.domain.entity.image.ImageFile;
import com.example.demo.infrastructure.repository.entity.image.ImageFileEntityMongo;

public class ImageFileMapperDB {
    private ImageFileMapperDB() {}

    public static ImageFileEntityMongo toEntity(ImageFile domain) {
        return ImageFileEntityMongo.builder()
                .id(domain.getId())
                .filename(domain.getFilename())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .url(domain.getUrl())
                .build();
    }

    public static ImageFile toDomain(ImageFileEntityMongo entity) {
        return ImageFile.builder()
                .id(entity.getId())
                .filename(entity.getFilename())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .url(entity.getUrl())
                .build();
    }
}