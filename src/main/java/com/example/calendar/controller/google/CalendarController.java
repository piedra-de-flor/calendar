package com.example.calendar.controller.google;

import com.example.calendar.service.schedule.TaskFacadeService;
import com.example.calendar.service.schedule.google.CalendarService;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CalendarController {
    private final TaskFacadeService taskFacadeService;

    //http://localhost:8080/oauth2/authorization/google
    @GetMapping("/calendar/tasks/month")
    public ResponseEntity<Boolean> getUpcomingEvents(@RequestParam String email) throws IOException, GeneralSecurityException {
        boolean response = taskFacadeService.mapEventsToTasks(email, LocalDate.now());
        return ResponseEntity.ok(response);
    }
}

