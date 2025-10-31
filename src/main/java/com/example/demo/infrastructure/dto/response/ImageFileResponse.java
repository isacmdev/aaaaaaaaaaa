package com.example.demo.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFileResponse {
    private String id;
    private String filename;
    private String contentType;
    private Long size;
    private String url;
}
