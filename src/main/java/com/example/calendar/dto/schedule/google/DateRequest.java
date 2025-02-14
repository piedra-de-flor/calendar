package com.example.calendar.dto.schedule.google;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record DateRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
) {}