package com.example.calendar.dto.schedule.google;

import java.time.LocalDate;
import java.time.LocalTime;

public record GoogleTaskCreateDto (
        long categoryId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description,
        String eventId
) {
}
