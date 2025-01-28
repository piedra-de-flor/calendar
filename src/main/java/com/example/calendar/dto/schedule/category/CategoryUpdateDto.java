package com.example.calendar.dto.schedule.category;

import com.example.calendar.dto.exception.RequestValidationException;

import java.util.Objects;

public record CategoryUpdateDto (
        long categoryId,
        String name,
        String color
) {
    public CategoryUpdateDto {
        requireNonNull(name, "Name must not be null");
        requireNonNull(color, "You should choose one color");

        if (name.length() >= 10) {
            throw new RequestValidationException("Name must be smaller than 10 character");
        }

        if (name.startsWith(" ") || name.endsWith(" ")) {
            throw new RequestValidationException("Name must not start or end with blank");
        }
    }

    private static <T> void requireNonNull(T obj, String message) {
        if (obj == null)
            throw new RequestValidationException(message);
    }
}
