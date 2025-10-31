package com.example.demo.infrastructure.repository.mapper.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

@NoArgsConstructor
public class ImagesConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @WritingConverter
    public static class ListToStringConverter implements Converter<List<String>, String> {
        @Override
        public String convert(List<String> source) {
            try {
                return objectMapper.writeValueAsString(source);
            } catch (Exception e) {
                throw new RuntimeException("Error converting List<String> to JSON string", e);
            }
        }
    }

    @ReadingConverter
    public static class StringToListConverter implements Converter<String, List<String>> {
        @Override
        public List<String> convert(String source) {
            try {
                return objectMapper.readValue(source, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error converting JSON string to List<String>", e);
            }
        }
    }
}
