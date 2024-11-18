package com.example.calendar.domain.entity.schedule;

import com.example.calendar.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;

    @Builder
    public Task(Member member, Category category, LocalDate date, LocalTime startTime, LocalTime endTime, String description) {
        this.member = member;
        this.category = category;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }
}
