package com.example.calendar.domain.entity.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.vo.vote.VoteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class VoteTest {

    private Vote vote;
    private Team mockTeam;
    private VoteOption option1;
    private VoteOption option2;

    @BeforeEach
    void setUp() {
        // Mock Team 객체 생성
        mockTeam = Team.builder().name("Mock Team").build();

        // VoteOption 객체 생성
        option1 = VoteOption.builder().optionText("Option 1").build();
        option2 = VoteOption.builder().optionText("Option 2").build();

        // Vote 초기화
        vote = Vote.builder()
                .team(mockTeam)
                .title("Sample Vote")
                .description("Sample Description")
                .isMultipleChoice(true)
                .options(List.of(option1, option2))
                .build();
    }

    @Test
    void 투표_생성_테스트() {
        // Then
        assertThat(vote.getTitle()).isEqualTo("Sample Vote");
        assertThat(vote.getDescription()).isEqualTo("Sample Description");
        assertThat(vote.isMultipleChoice()).isTrue();
        assertThat(vote.getOptions()).containsExactly(option1, option2);
        assertThat(vote.getStatus()).isEqualTo(VoteStatus.OPEN);
        assertThat(vote.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(vote.getClosedAt()).isAfter(LocalDateTime.now());
        assertThat(vote.getTeam()).isEqualTo(mockTeam);
    }

    @Test
    void 투표_상태_오픈_검증_테스트() {
        // When
        boolean isOpen = vote.isOpen();

        // Then
        assertThat(isOpen).isTrue();
    }

    @Test
    void 투표_종료_테스트() {
        // When
        vote.close();

        // Then
        assertThat(vote.getStatus()).isEqualTo(VoteStatus.CLOSED);
        assertThat(vote.getClosedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 이미_종료된_투표_종료_예외_테스트() {
        // Given
        vote.close();

        // When & Then
        assertThatThrownBy(vote::close)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The vote is already closed.");
    }

    @Test
    void 투표_옵션_연결_테스트() {
        assertThat(vote.getOptions()).containsExactly(option1, option2);
    }

    @Test
    void 다중_선택_테스트() {
        // When
        vote = Vote.builder()
                .team(mockTeam)
                .title("Multiple Choice Vote")
                .description("Description")
                .isMultipleChoice(true)
                .options(List.of(option1, option2))
                .build();

        // Then
        assertThat(vote.isMultipleChoice()).isTrue();
    }

    @Test
    void 단일_선택_테스트() {
        // When
        vote = Vote.builder()
                .team(mockTeam)
                .title("Single Choice Vote")
                .description("Description")
                .isMultipleChoice(false)
                .options(List.of(option1, option2))
                .build();

        // Then
        assertThat(vote.isMultipleChoice()).isFalse();
    }
}
