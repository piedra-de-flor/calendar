package com.example.calendar.service.schedule.google;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.GoogleCalendarTask;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.dto.schedule.google.GoogleTaskCreateDto;
import com.example.calendar.repository.CategoryRepository;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoogleTaskServiceTest {
    @InjectMocks
    private GoogleTaskService googleTaskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 구굴_일정_생성_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        GoogleTaskCreateDto taskCreateDto = new GoogleTaskCreateDto(
                1L,
                LocalDate.of(2023, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Test Task",
                "event123"
        );

        Member member = mock(Member.class);
        Category category = mock(Category.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(taskCreateDto.categoryId())).thenReturn(Optional.of(category));
        when(member.getTasks()).thenReturn(List.of());

        // When
        googleTaskService.createGoogleTask(memberEmail, taskCreateDto);

        // Then
        verify(taskRepository, times(1)).save(any(GoogleCalendarTask.class));
        verify(member, times(1)).addTask(any(GoogleCalendarTask.class));
    }

    @Test
    void 구굴_일정_생성중_회원_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";
        GoogleTaskCreateDto taskCreateDto = new GoogleTaskCreateDto(
                1L,
                LocalDate.of(2023, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Test Task",
                "event123"
        );

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> googleTaskService.createGoogleTask(memberEmail, taskCreateDto))
                .isInstanceOf(NoSuchElementException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void 구굴_일정_생성중_카테고리_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";
        GoogleTaskCreateDto taskCreateDto = new GoogleTaskCreateDto(
                1L,
                LocalDate.of(2023, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Test Task",
                "event123"
        );

        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(taskCreateDto.categoryId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> googleTaskService.createGoogleTask(memberEmail, taskCreateDto))
                .isInstanceOf(NoSuchElementException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void 구굴_일정_생성_중복_이벤트_테스트() {
        // Given
        String memberEmail = "user@example.com";
        GoogleTaskCreateDto taskCreateDto = new GoogleTaskCreateDto(
                1L,
                LocalDate.of(2023, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Test Task",
                "event123"
        );

        Member member = mock(Member.class);
        Category category = mock(Category.class);
        GoogleCalendarTask existingTask = mock(GoogleCalendarTask.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(taskCreateDto.categoryId())).thenReturn(Optional.of(category));
        when(member.getTasks()).thenReturn(List.of(existingTask));
        when(existingTask.getEventId()).thenReturn("event123");
        when(existingTask.getDate()).thenReturn(LocalDate.of(2023, 1, 1));
        when(existingTask.getStartTime()).thenReturn(LocalTime.of(10, 0));
        when(existingTask.getEndTime()).thenReturn(LocalTime.of(11, 0));

        // When
        googleTaskService.createGoogleTask(memberEmail, taskCreateDto);

        // Then
        verify(taskRepository, never()).delete(existingTask);
        verify(taskRepository, never()).save(any(GoogleCalendarTask.class));
        verify(member, never()).addTask(any(GoogleCalendarTask.class));
    }

    @Test
    void 구굴_일정_생성_중복_이벤트_삭제_테스트() {
        // Given
        String memberEmail = "user@example.com";
        GoogleTaskCreateDto taskCreateDto = new GoogleTaskCreateDto(
                1L,
                LocalDate.of(2023, 1, 1),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                "Updated Task",
                "event123"
        );

        Member member = mock(Member.class);
        Category category = mock(Category.class);
        GoogleCalendarTask existingTask = mock(GoogleCalendarTask.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(taskCreateDto.categoryId())).thenReturn(Optional.of(category));
        when(member.getTasks()).thenReturn(List.of(existingTask));
        when(existingTask.getEventId()).thenReturn("event123");
        when(existingTask.getDate()).thenReturn(LocalDate.of(2023, 1, 1));
        when(existingTask.getStartTime()).thenReturn(LocalTime.of(10, 0));
        when(existingTask.getEndTime()).thenReturn(LocalTime.of(11, 0));

        // When
        googleTaskService.createGoogleTask(memberEmail, taskCreateDto);

        // Then
        verify(taskRepository, times(1)).delete(existingTask);
        verify(member, times(1)).deleteTask(existingTask);
        verify(taskRepository, times(1)).save(any(GoogleCalendarTask.class));
        verify(member, times(1)).addTask(any(GoogleCalendarTask.class));
    }
}
