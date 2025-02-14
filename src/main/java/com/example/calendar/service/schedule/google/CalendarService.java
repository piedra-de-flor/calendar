package com.example.calendar.service.schedule.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarService {

    private static final String APPLICATION_NAME = "Google Calendar API";

    private final GoogleAuthorizationService googleAuthorizationService;

    public List<Event> getUpcomingEventsForCurrentMonth(String email, LocalDate date) throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                googleAuthorizationService.getCredentials(email))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ZonedDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endOfMonth = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul"));

        DateTime timeMin = new DateTime(startOfMonth.toInstant().toEpochMilli());
        DateTime timeMax = new DateTime(endOfMonth.toInstant().toEpochMilli());
        log.info("Fetching events from {} to {}", timeMin, timeMax);

        Events events = service.events().list("primary")
                .setMaxResults(500)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setTimeZone("Asia/Seoul")
                .execute();

        return events.getItems();
    }
}

