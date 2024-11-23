package com.example.calendar.dto.schedule.task.freetime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class TimeBlock {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
