package com.example.calendar.dto.schedule.task;

import com.example.calendar.dto.exception.RequestValidationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record TaskUpdateDto (
        long taskId,
        long categoryId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description
) {
    public TaskUpdateDto {
        requireNonNull(date, "Date must not be null");
        requireNonNull(description, "Title must not be null");
        requireNonNull(startTime, "startTime must not be null");
        requireNonNull(endTime, "endTime must not be null");

        if (description.length() >= 10) {
            throw new RequestValidationException("Title must be smaller than 10 character");
        }

        if (description.startsWith(" ") || description.endsWith(" ")) {
            throw new RequestValidationException("Title must not start or end with blank");
        }

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RequestValidationException("start time must be earlier than end time");
        }
    }

    private static <T> void requireNonNull(T obj, String message) {
        if (obj == null)
            throw new RequestValidationException(message);
    }
}
