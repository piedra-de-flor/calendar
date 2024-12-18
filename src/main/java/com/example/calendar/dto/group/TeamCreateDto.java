package com.example.calendar.dto.group;

import java.util.List;
import java.util.Objects;

public record TeamCreateDto(
        List<Long> friends,
        String name
) {
    public TeamCreateDto {
        Objects.requireNonNull(name, "Name must not be null");

        if (name.isEmpty() || name.length() > 10) {
            throw new IllegalArgumentException("Group name must be between 1 and 10 characters");
        }

        if (name.startsWith(" ") || name.endsWith(" ")) {
            throw new IllegalArgumentException("Group name should not start or end with blank");
        }
    }
}
