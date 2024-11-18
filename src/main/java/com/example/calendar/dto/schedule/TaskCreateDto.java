package com.example.calendar.dto.schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record TaskCreateDto (
        long categoryId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description
){
}
