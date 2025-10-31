package com.example.demo.infrastructure.repository.entity.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "files_metadata")
public class ImageFileEntityMongo {

    @Id
    private String id;

    private String filename;
    private String contentType;
    private Long size;
    private String url;
}