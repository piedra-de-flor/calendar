package com.example.calendar.service.schedule;

import com.example.calendar.domain.vo.schedule.EmptyCategoryId;
import com.example.calendar.dto.schedule.task.TaskCreateDto;
import com.example.calendar.service.schedule.google.CalendarService;
import com.example.calendar.service.schedule.task.TaskService;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class TaskFacadeService {
    private final CalendarService calendarService;
    private final TaskService taskService;

    public boolean mapEventsToTasks(String memberEmail, LocalDate benchmarkDate) throws GeneralSecurityException, IOException {
        List<Event> events = calendarService.getUpcomingEventsForCurrentMonth(memberEmail, benchmarkDate);

        for (Event event : events) {
            EventDateTime start = event.getStart();
            EventDateTime end = event.getEnd();

            if (start == null || end == null || start.getDateTime() == null || end.getDateTime() == null) {
                log.warn("Skipping event with missing start or end time: {}", event.getSummary());
                continue;
            }

            LocalDate date = Instant.ofEpochMilli(start.getDateTime().getValue())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDate();

            LocalTime startTime = Instant.ofEpochMilli(start.getDateTime().getValue())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalTime();

            LocalTime endTime = Instant.ofEpochMilli(end.getDateTime().getValue())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalTime();

            TaskCreateDto createDto = new TaskCreateDto(
                    EmptyCategoryId.EMPTY_CATEGORY_ID.getValue(),
                    date,
                    startTime,
                    endTime,
                    event.getSummary()
            );

            taskService.createTask(memberEmail, createDto);
        }

        return true;
    }
}
