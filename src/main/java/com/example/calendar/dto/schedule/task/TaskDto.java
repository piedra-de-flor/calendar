package com.example.calendar.dto.schedule.task;

import java.time.LocalTime;

public record TaskDto (
        long taskId,
        long categoryId,
        String categoryName,
        String categoryColor,
        LocalTime start,
        LocalTime end,
        String description
){
}
