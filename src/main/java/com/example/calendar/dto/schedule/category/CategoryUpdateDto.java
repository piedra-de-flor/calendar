package com.example.calendar.dto.schedule.category;

import java.util.Objects;

public record CategoryUpdateDto (
        long categoryId,
        String name,
        String color
) {
    public CategoryUpdateDto {
        Objects.requireNonNull(name, "Name must not be null");
        Objects.requireNonNull(color, "You should choose one color");

        if (name.length() < 10) {
            throw new IllegalArgumentException("Name must be smaller than 10 character");
        }

        if (name.startsWith(" ") || name.endsWith(" ")) {
            throw new IllegalArgumentException("Name must not start or end with blank");
        }
    }
}
