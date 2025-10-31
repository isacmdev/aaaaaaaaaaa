package com.example.demo.application.ex.validator;

import com.example.demo.application.ex.MissingRequiredException;
import com.example.demo.domain.entity.product.Products;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ProductValidator {

    public void validateCommon(Products product) {
        if (product == null) {
            throw new MissingRequiredException("El producto no puede ser nulo.");
        }

        if (!StringUtils.hasText(product.getName())) {
            throw new MissingRequiredException("El nombre del producto es obligatorio y no puede estar vacío.");
        }

        if (product.getName().length() > 255) {
            throw new MissingRequiredException("El nombre del producto no puede superar los 255 caracteres.");
        }

        if (!StringUtils.hasText(product.getDescription())) {
            throw new MissingRequiredException("La descripcion del producto es obligatoria y no puede estar vacía.");
        }

        if (product.getDescription() != null && product.getDescription().length() > 1000) {
            throw new MissingRequiredException("La descripción no puede superar los 1000 caracteres.");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new MissingRequiredException("El precio debe ser un valor positivo.");
        }

        if (product.getStock() <= 0) {
            throw new MissingRequiredException("El stock no puede ser menor que 0.");
        }

        List<String> images = product.getImages();

        if (images == null || images.isEmpty()) {
            throw new MissingRequiredException("Se requiere al menos una imagen para el producto.");
        }

        if (images.size() > 5) {
            throw new MissingRequiredException("No se permiten más de 5 imágenes por producto.");
        }

        boolean hasInvalid = images.stream().anyMatch(url -> !StringUtils.hasText(url));
        if (hasInvalid) {
            throw new MissingRequiredException("Las URLs de imágenes no pueden estar vacías.");
        }
    }
}