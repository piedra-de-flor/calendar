package com.example.calendar.dto.schedule;

import java.time.LocalTime;

public record TaskDto (
        String categoryName,
        String categoryColor,
        LocalTime start,
        LocalTime end,
        String description
){
}
