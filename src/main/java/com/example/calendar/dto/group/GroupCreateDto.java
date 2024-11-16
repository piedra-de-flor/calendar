package com.example.calendar.dto.group;

import java.util.List;
import java.util.Objects;

public record GroupCreateDto(
        List<Long> friends,
        String name
) {
    public GroupCreateDto {
        Objects.requireNonNull(name, "Name must not be null");

        if (name.isEmpty() || name.length() > 10) {
            throw new IllegalArgumentException("group name must be between 1 and 10 characters");
        }
    }
}
