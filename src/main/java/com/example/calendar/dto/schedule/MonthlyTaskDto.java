package com.example.calendar.dto.schedule;

import java.util.List;

public record MonthlyTaskDto (
        List<DailyTaskDto> taskDtos
){
}
