package com.example.calendar.dto.group;

import com.example.calendar.dto.exception.RequestValidationException;

import java.util.List;

public record TeamCreateDto(
        List<Long> friends,
        String name
) {
    public TeamCreateDto {
        requireNonNull(name, "Name must not be null");

        if (name.isEmpty() || name.length() > 10) {
            throw new RequestValidationException("Group name must be between 1 and 10 characters");
        }

        if (name.startsWith(" ") || name.endsWith(" ")) {
            throw new RequestValidationException("Group name should not start or end with blank");
        }
    }

    private static <T> void requireNonNull(T obj, String message) {
        if (obj == null)
            throw new RequestValidationException(message);
    }
}
