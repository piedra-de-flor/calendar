package com.example.calendar.service.google;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarService {

    private static final String APPLICATION_NAME = "Google Calendar API";

    private final GoogleAuthorizationService googleAuthorizationService;

    public List<Event> getUpcomingEventsForCurrentMonth(String email) throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                googleAuthorizationService.getCredentials(email))
                .setApplicationName(APPLICATION_NAME)
                .build();


        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")); // 한국 시간대 사용
        ZonedDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul"));

        DateTime timeMin = new DateTime(startOfMonth.toInstant().toEpochMilli());
        DateTime timeMax = new DateTime(endOfMonth.toInstant().toEpochMilli());
        log.info("Fetching events from {} to {}", timeMin, timeMax);

        // Google Calendar API 호출
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

