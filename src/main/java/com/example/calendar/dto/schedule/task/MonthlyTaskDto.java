package com.example.calendar.dto.schedule.task;

import java.util.List;

public record MonthlyTaskDto (
        List<DailyTaskDto> taskDtos
){
}
