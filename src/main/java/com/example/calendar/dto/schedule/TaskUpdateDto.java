package com.example.calendar.dto.schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record TaskUpdateDto (
        long taskId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description
) {
}
