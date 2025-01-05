package com.example.calendar.dto.schedule.task;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskUpdateDtoTest {

    @Test
    void 모든_필드가_유효한_경우_생성_성공_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        String description = "ValidTask";

        // When
        TaskUpdateDto dto = new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, description);

        // Then
        assertThat(dto.taskId()).isEqualTo(taskId);
        assertThat(dto.categoryId()).isEqualTo(categoryId);
        assertThat(dto.date()).isEqualTo(date);
        assertThat(dto.startTime()).isEqualTo(startTime);
        assertThat(dto.endTime()).isEqualTo(endTime);
        assertThat(dto.description()).isEqualTo(description);
    }

    @Test
    void 날짜가_null일_경우_예외_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        String description = "ValidTask";

        // When & Then
        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, null, startTime, endTime, description))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Date must not be null");
    }

    @Test
    void 설명이_null일_경우_예외_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        // When & Then
        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Title must not be null");
    }

    @Test
    void 시작_시간이_끝_시간_이후일_경우_예외_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        String description = "ValidTask";

        // When & Then
        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start time must be earlier than end time");
    }

    @Test
    void 시작_시간과_끝_시간이_같을_경우_예외_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        String description = "ValidTask";

        // When & Then
        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start time must be earlier than end time");
    }

    @Test
    void 설명_길이가_10자_이상일_경우_예외_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        String description = "VeryLongTaskName"; // 10자 이상

        // When & Then
        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title must be smaller than 10 character");
    }

    @Test
    void 설명이_공백으로_시작하거나_끝날_경우_예외_테스트() {
        // Given
        long taskId = 1L;
        long categoryId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        // When & Then
        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, " Invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title must not start or end with blank");

        assertThatThrownBy(() -> new TaskUpdateDto(taskId, categoryId, date, startTime, endTime, "Invalid "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title must not start or end with blank");
    }
}
