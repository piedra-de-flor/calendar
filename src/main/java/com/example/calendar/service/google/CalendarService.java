package com.example.calendar.service.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class CalendarService {

    private static final String APPLICATION_NAME = "Google Calendar API Spring Boot Example";

    private final GoogleAuthorizationService googleAuthorizationService;

    @Autowired
    public CalendarService(GoogleAuthorizationService googleAuthorizationService) {
        this.googleAuthorizationService = googleAuthorizationService;
    }

    /**
     * Retrieve upcoming events for a specific user by email.
     */
    public List<Event> getUpcomingEvents(String email) throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                googleAuthorizationService.getCredentials(email))
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
}

