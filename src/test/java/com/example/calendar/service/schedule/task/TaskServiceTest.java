package com.example.calendar.service.schedule.task;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.dto.schedule.task.DailyTaskDto;
import com.example.calendar.dto.schedule.task.TaskCreateDto;
import com.example.calendar.dto.schedule.task.TaskUpdateDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;
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
    void 일정_생성_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        TaskCreateDto createDto = new TaskCreateDto(
                1L,
                LocalDate.of(2023, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "Test Task"
        );

        Member member = mock(Member.class);
        Category category = mock(Category.class);
        Task savedTask = mock(Task.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(createDto.categoryId())).thenReturn(Optional.of(category));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(savedTask.getId()).thenReturn(1L);

        // When
        long result = taskService.createTask(memberEmail, createDto);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(member, times(1)).addTask(any(Task.class));
    }

    @Test
    void 일정_삭제_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long taskId = 1L;

        Member member = mock(Member.class);
        Task task = mock(Task.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);

        // When
        boolean result = taskService.deleteTask(memberEmail, taskId);

        // Then
        assertThat(result).isTrue();
        verify(taskRepository, times(1)).delete(task);
        verify(member, times(1)).deleteTask(task);
    }

    @Test
    void 일정_삭제_권한_예외_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long taskId = 1L;

        Member member = mock(Member.class);
        Member anotherMember = mock(Member.class);
        Task task = mock(Task.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(task.getMember()).thenReturn(anotherMember);
        when(member.getId()).thenReturn(1L);
        when(anotherMember.getId()).thenReturn(2L);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(memberEmail, taskId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("you don't have auth to delete task");

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void 일별_일정_조회_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        LocalDate date = LocalDate.of(2023, 1, 1);

        Member member = mock(Member.class);
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);
        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getTasks()).thenReturn(List.of(task1, task2));

        // Mock Task1
        when(task1.getDate()).thenReturn(date);
        when(task1.getCategory()).thenReturn(category1);
        when(category1.getId()).thenReturn(1L);
        when(category1.getCategoryName()).thenReturn("Work");
        when(category1.getCategoryColor()).thenReturn("RED");
        when(task1.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(task1.getEndTime()).thenReturn(LocalTime.of(10, 0));
        when(task1.getDescription()).thenReturn("Task 1 Description");

        // Mock Task2
        when(task2.getDate()).thenReturn(date);
        when(task2.getCategory()).thenReturn(category2);
        when(category2.getId()).thenReturn(2L);
        when(category2.getCategoryName()).thenReturn("Personal");
        when(category2.getCategoryColor()).thenReturn("BLUE");
        when(task2.getStartTime()).thenReturn(LocalTime.of(11, 0));
        when(task2.getEndTime()).thenReturn(LocalTime.of(12, 0));
        when(task2.getDescription()).thenReturn("Task 2 Description");

        // When
        DailyTaskDto result = taskService.readDailyTasks(memberEmail, date);

        // Then
        assertThat(result.taskDtos()).hasSize(2);
        verify(memberRepository, times(1)).findByEmail(memberEmail);
    }


    @Test
    void 일정_수정_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        TaskUpdateDto updateDto = new TaskUpdateDto(
                1L,
                2L,
                LocalDate.of(2023, 1, 2),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Updated"
        );

        Member member = mock(Member.class);
        Task task = mock(Task.class);
        Category category = mock(Category.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(taskRepository.findById(updateDto.taskId())).thenReturn(Optional.of(task));
        when(categoryRepository.findById(updateDto.categoryId())).thenReturn(Optional.of(category));
        when(task.getMember()).thenReturn(member);
        when(task.getId()).thenReturn(1L);
        when(member.getId()).thenReturn(1L);

        // When
        long result = taskService.updateTask(memberEmail, updateDto);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(task, times(1)).update(updateDto, category);
    }
}
