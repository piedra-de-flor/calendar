package com.example.calendar.dto.schedule.task;

import java.util.List;

public record DailyTaskDto (
        List<TaskDto> taskDtos
) {
}
