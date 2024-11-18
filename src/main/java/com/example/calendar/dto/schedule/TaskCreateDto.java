package com.example.calendar.dto.schedule;

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
    }
}
