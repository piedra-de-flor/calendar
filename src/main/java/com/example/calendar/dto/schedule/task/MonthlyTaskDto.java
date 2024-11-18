package com.example.calendar.dto.schedule.task;

import com.example.calendar.dto.schedule.task.DailyTaskDto;

import java.util.List;

public record MonthlyTaskDto (
        List<DailyTaskDto> taskDtos
){
}
