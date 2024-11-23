package com.example.calendar.controller.google;

import com.example.calendar.dto.schedule.google.DateRequest;
import com.example.calendar.service.schedule.google.GoogleFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@RestController
public class GoogleCalendarController {
    private final GoogleFacadeService googleFacadeService;

    //http://localhost:8080/oauth2/authorization/google
    @GetMapping("/google-calendar/tasks/month")
    public ResponseEntity<Boolean> getUpcomingEvents(@RequestBody DateRequest request) throws IOException, GeneralSecurityException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = googleFacadeService.mapEventsToTasks(memberEmail, request.date());
        return ResponseEntity.ok(response);
    }
}

