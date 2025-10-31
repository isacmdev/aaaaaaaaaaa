package com.example.demo.infrastructure.repository.mapper.util;

import com.example.demo.infrastructure.ex.InfrastructureException;
import com.example.demo.infrastructure.repository.entity.product.ProductEntityDB;
import io.r2dbc.spi.Row;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@UtilityClass
public class ProductEntityRowMapper {

    public static ProductEntityDB fromRow(Row row) {
        if (row == null) return null;

        return ProductEntityDB.builder()
                .id(row.get("id", Long.class))
                .name(row.get("name", String.class))
                .description(row.get("description", String.class))
                .images(row.get("images", String.class))
                .price(row.get("price", Double.class))
                .stock(row.get("stock", Integer.class))
                .createdAt(readLocalDateTime(row, "created_at"))
                .updatedAt(readLocalDateTime(row, "updated_at"))
                .build();
    }

    private static LocalDateTime readLocalDateTime(Row row, String column) {
        if (!hasColumn(row, column)) return null;
        Object obj = row.get(column);
        if (obj == null) return null;

        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        if (obj instanceof OffsetDateTime) {
            return ((OffsetDateTime) obj).toLocalDateTime();
        }
        if (obj instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) obj, java.time.ZoneId.systemDefault());
        }
        if (obj instanceof Timestamp) {
            return ((Timestamp) obj).toLocalDateTime();
        }
        if (obj instanceof String) {
            try {
                return LocalDateTime.parse((String) obj);
            } catch (Exception ignored) {
                throw new InfrastructureException("Failed to parse LocalDateTime from String for column: " + column);
            }
        }
        return null;
    }

    private static boolean hasColumn(Row row, String columnName) {
        try {
            return row.get(columnName) != null || row.get(columnName, Object.class) == null;
        } catch (Exception e) {
            return false;
        }
    }
}
