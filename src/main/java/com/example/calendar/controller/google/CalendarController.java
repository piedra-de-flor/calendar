package com.example.calendar.controller.google;

import com.example.calendar.service.schedule.TaskFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CalendarController {
    private final TaskFacadeService taskFacadeService;

    //http://localhost:8080/oauth2/authorization/google
    @GetMapping("/calendar/tasks/month")
    public ResponseEntity<Boolean> getUpcomingEvents(@RequestBody LocalDate date) throws IOException, GeneralSecurityException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = taskFacadeService.mapEventsToTasks(memberEmail, date);
        return ResponseEntity.ok(response);
    }
}

