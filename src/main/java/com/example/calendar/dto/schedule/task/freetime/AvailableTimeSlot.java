package com.example.calendar.dto.schedule.task.freetime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AvailableTimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> availableMembers;
}