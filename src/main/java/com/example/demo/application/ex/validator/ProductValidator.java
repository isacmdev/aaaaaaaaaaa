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

        validateName(product.getName());
        validateCategory(product.getCategory());
        validateInternalCode(product.getInternalCode());
        validateDescription(product.getDescription());
        validatePrice(product.getPrice());
        validateStock(product.getStock());
        validateImages(product.getImages());
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new MissingRequiredException("El nombre del producto es obligatorio y no puede estar vacío.");
        }
        if (name.length() > 255) {
            throw new MissingRequiredException("El nombre del producto no puede superar los 255 caracteres.");
        }
    }

    private void validateCategory(String category) {
        if (!StringUtils.hasText(category)) {
            throw new MissingRequiredException("La categoría no puede estar vacía.");
        }
        if (category.length() > 150) {
            throw new MissingRequiredException("La categoría del producto no puede superar los 150 caracteres.");
        }
    }

    private void validateInternalCode(String internalCode) {
        if (!StringUtils.hasText(internalCode)) {
            throw new MissingRequiredException("El código interno del producto es obligatorio y no puede estar vacío.");
        }
    }

    private void validateDescription(String description) {
        if (!StringUtils.hasText(description)) {
            throw new MissingRequiredException("La descripción del producto es obligatoria y no puede estar vacía.");
        }
        if (description.length() > 1000) {
            throw new MissingRequiredException("La descripción no puede superar los 1000 caracteres.");
        }
    }

    private void validatePrice(Double price) {
        if (price == null || price <= 0) {
            throw new MissingRequiredException("El precio debe ser un valor positivo.");
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new MissingRequiredException("El stock no puede ser menor que 0.");
        }
    }

    private void validateImages(List<String> images) {
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
