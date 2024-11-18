package com.example.calendar.dto.schedule.category;

public record CategoryUpdateDto (
        long categoryId,
        String name,
        String color
) {
}
