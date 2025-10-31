package com.example.demo.domain.entity.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFile {
    private String id;
    private String filename;
    private String contentType;
    private Long size;
    private String url;
}
