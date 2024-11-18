package com.example.calendar.dto.schedule;

import java.util.List;

public record DailyTaskDto (
        List<TaskDto> taskDtos
) {
}
