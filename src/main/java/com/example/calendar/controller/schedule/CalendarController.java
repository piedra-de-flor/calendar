package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.task.freetime.AvailableTimeSlot;
import com.example.calendar.service.schedule.TeamTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CalendarController {
    private final TeamTaskService teamTaskService;

    @GetMapping("/{teamId}/available-slots")
    public List<AvailableTimeSlot> getAvailableTimeSlots(
            @PathVariable Long teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam int minDurationMinutes,
            @RequestParam int minMembers,
            @RequestParam int minGapMinutes,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime availableFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime availableTo) {

        return teamTaskService.findAvailableTimeSlots(teamId, startDate, endDate,availableFrom, availableTo,Duration.ofMinutes(minDurationMinutes), Duration.ofMinutes(minGapMinutes), minMembers);
    }
}