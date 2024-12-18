package com.example.calendar.dto.schedule.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record TaskCreateDto (
        long categoryId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description
){
    public TaskCreateDto {
        Objects.requireNonNull(date, "Date must not be null");
        Objects.requireNonNull(description, "Title must not be null");
        Objects.requireNonNull(startTime, "startTime must not be null");
        Objects.requireNonNull(endTime, "endTime must not be null");

        if (description.length() < 10) {
            throw new IllegalArgumentException("Title must be smaller than 10 character");
        }

        if (description.startsWith(" ") || description.endsWith(" ")) {
            throw new IllegalArgumentException("Title must not start or end with blank");
        }

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("start time must be earlier than end time");
        }
    }
}
