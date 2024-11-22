package com.example.calendar.controller.google;

import com.example.calendar.service.google.CalendarService;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    //http://localhost:8080/oauth2/authorization/google
    @GetMapping("/events")
    public List<Event> getUpcomingEvents(@RequestParam String email) throws IOException, GeneralSecurityException {
        return calendarService.getUpcomingEventsForCurrentMonth(email);
    }
}

