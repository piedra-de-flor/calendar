package com.example.calendar.service.schedule;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.domain.vo.schedule.CategoryInfo;
import com.example.calendar.dto.schedule.task.freetime.AvailableTimeSlot;
import com.example.calendar.repository.TaskRepository;
import com.example.calendar.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TeamTaskServiceTest {
    @InjectMocks
    private TeamTaskService teamTaskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 공통_여유시간_계산_테스트() {
        // Given
        Long teamId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 1);
        LocalTime availableFrom = LocalTime.of(9, 0);
        LocalTime availableTo = LocalTime.of(18, 0);
        Duration minDuration = Duration.ofMinutes(1);
        Duration minGap = Duration.ofMinutes(10);
        int minMembers = 2;

        // Team setup
        Team team = new Team("TeamName");
        Member alice = new Member("Alice", "test@test.com", "testpw", null, null);
        Member bob = new Member("Bob", "test2@test.com", "testpw", null, null);

        Teaming teamingAlice = new Teaming(alice, team);
        Teaming teamingBob = new Teaming(bob, team);

        team.addTeaming(teamingAlice);
        team.addTeaming(teamingBob);

        Category category1 = new Category(alice, new CategoryInfo("test", "test"));
        Category category2 = new Category(bob, new CategoryInfo("test", "test"));

        // Task setup
        Task task1 = new Task(alice, category1, startDate, LocalTime.of(10, 0), LocalTime.of(11, 0), "Alice Task 1");
        Task task2 = new Task(alice, category1, startDate,LocalTime.of(14, 0), LocalTime.of(15, 0), "Alice Task 2");
        Task task3 = new Task(bob, category2, startDate, LocalTime.of(12, 0), LocalTime.of(13, 0), "Bob Task 1");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(taskRepository.findTasksByMembersAndDateRange(anyList(), any(), any()))
                .thenReturn(List.of(task1, task2, task3));

        // When
        List<AvailableTimeSlot> result = teamTaskService.findAvailableTimeSlots(
                teamId, startDate, endDate, availableFrom, availableTo, minDuration, minGap, minMembers
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(4);

        AvailableTimeSlot slot1 = result.get(0);
        assertThat(slot1.getStart()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(9, 10)));
        assertThat(slot1.getEnd()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(10, 0)));
        assertThat(slot1.getAvailableMembers()).containsExactlyInAnyOrder("Alice", "Bob");

        AvailableTimeSlot slot2 = result.get(1);
        assertThat(slot2.getStart()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(11, 10)));
        assertThat(slot2.getEnd()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(12, 0)));
        assertThat(slot2.getAvailableMembers()).containsExactlyInAnyOrder("Alice", "Bob");

        AvailableTimeSlot slot3 = result.get(2);
        assertThat(slot3.getStart()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(13, 10)));
        assertThat(slot3.getEnd()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(14, 0)));
        assertThat(slot3.getAvailableMembers()).containsExactlyInAnyOrder("Alice", "Bob");

        AvailableTimeSlot slot4 = result.get(3);
        assertThat(slot4.getStart()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(15, 10)));
        assertThat(slot4.getEnd()).isEqualTo(LocalDateTime.of(startDate, LocalTime.of(18, 0)));
        assertThat(slot4.getAvailableMembers()).containsExactlyInAnyOrder("Alice", "Bob");

        // Verify interactions with repositories
        verify(teamRepository, times(1)).findById(teamId);
        verify(taskRepository, times(1)).findTasksByMembersAndDateRange(anyList(), eq(startDate), eq(endDate));
    }

    @Test
    void 공통_여유_시간_찾기_팀_조회_실패_테스트() {
        // Given
        Long teamId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 1);
        LocalTime availableFrom = LocalTime.of(9, 0);
        LocalTime availableTo = LocalTime.of(18, 0);
        Duration minDuration = Duration.ofHours(1);
        Duration minGap = Duration.ofMinutes(30);
        int minMembers = 2;

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teamTaskService.findAvailableTimeSlots(
                teamId, startDate, endDate, availableFrom, availableTo, minDuration, minGap, minMembers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot find the team");

        verify(teamRepository, times(1)).findById(teamId);
        verify(taskRepository, never()).findTasksByMembersAndDateRange(anyList(), any(), any());
    }
}
