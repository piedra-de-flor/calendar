package com.example.calendar.domain.entity.schedule;

import com.example.calendar.domain.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class GoogleCalendarTask extends Task {
    @Column(nullable = false, unique = true)
    private String eventId;

    @Builder
    public GoogleCalendarTask(Member member, Category category, LocalDate date, LocalTime startTime, LocalTime endTime, String description, String eventId) {
        super(member, category, date, startTime, endTime, description);
        this.eventId = eventId;
    }
}