package com.example.calendar.domain.entity.schedule;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.schedule.task.TaskUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskTest {

    private Task task;
    private Member mockMember;
    private Category mockCategory;
    private Category newMockCategory;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        mockMember = mock(Member.class);
        mockCategory = mock(Category.class);
        newMockCategory = mock(Category.class);

        // 초기 Task 객체 생성
        task = Task.builder()
                .member(mockMember)
                .category(mockCategory)
                .date(LocalDate.of(2023, 1, 1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .description("Initial Description")
                .build();
    }

    @Test
    void 모든_필드_업데이트_테스트() {
        // Given
        TaskUpdateDto mockUpdateDto = mock(TaskUpdateDto.class);
        when(mockUpdateDto.date()).thenReturn(LocalDate.of(2023, 1, 2));
        when(mockUpdateDto.startTime()).thenReturn(LocalTime.of(10, 0));
        when(mockUpdateDto.endTime()).thenReturn(LocalTime.of(11, 0));
        when(mockUpdateDto.description()).thenReturn("Updated Description");

        // When
        task.update(mockUpdateDto, newMockCategory);

        // Then
        assertThat(task.getDate()).isEqualTo(LocalDate.of(2023, 1, 2));
        assertThat(task.getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(task.getEndTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(task.getDescription()).isEqualTo("Updated Description");
        assertThat(task.getCategory()).isEqualTo(newMockCategory);
    }

    @Test
    void 일부_필드_업데이트_테스트() {
        // Given
        TaskUpdateDto mockUpdateDto = mock(TaskUpdateDto.class);
        when(mockUpdateDto.date()).thenReturn(null);
        when(mockUpdateDto.startTime()).thenReturn(LocalTime.of(10, 0));
        when(mockUpdateDto.endTime()).thenReturn(null);
        when(mockUpdateDto.description()).thenReturn("Partially Updated Description");

        // When
        task.update(mockUpdateDto, newMockCategory);

        // Then
        assertThat(task.getDate()).isEqualTo(LocalDate.of(2023, 1, 1)); // 기존 값 유지
        assertThat(task.getStartTime()).isEqualTo(LocalTime.of(10, 0)); // 업데이트됨
        assertThat(task.getEndTime()).isEqualTo(LocalTime.of(10, 0)); // 기존 값 유지
        assertThat(task.getDescription()).isEqualTo("Partially Updated Description"); // 업데이트됨
        assertThat(task.getCategory()).isEqualTo(newMockCategory); // 업데이트됨
    }

    @Test
    void 카테고리_업데이트_테스트() {
        // When
        task.updateCategory(newMockCategory);

        // Then
        assertThat(task.getCategory()).isEqualTo(newMockCategory);
    }

    @Test
    void 업데이트_없이_기존값_유지_테스트() {
        // Given
        TaskUpdateDto mockUpdateDto = mock(TaskUpdateDto.class);
        when(mockUpdateDto.date()).thenReturn(null);
        when(mockUpdateDto.startTime()).thenReturn(null);
        when(mockUpdateDto.endTime()).thenReturn(null);
        when(mockUpdateDto.description()).thenReturn(null);

        // When
        task.update(mockUpdateDto, mockCategory);

        // Then
        assertThat(task.getDate()).isEqualTo(LocalDate.of(2023, 1, 1)); // 기존 값 유지
        assertThat(task.getStartTime()).isEqualTo(LocalTime.of(9, 0)); // 기존 값 유지
        assertThat(task.getEndTime()).isEqualTo(LocalTime.of(10, 0)); // 기존 값 유지
        assertThat(task.getDescription()).isEqualTo("Initial Description"); // 기존 값 유지
        assertThat(task.getCategory()).isEqualTo(mockCategory); // 기존 값 유지
    }
}
