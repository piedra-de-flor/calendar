package com.example.calendar.domain.entity.vote;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VoteOptionTest {

    private VoteOption voteOption;
    private Vote mockVote;

    @BeforeEach
    void setUp() {
        // Mock Vote 객체 생성
        mockVote = Vote.builder()
                .title("Mock Vote")
                .description("Mock Description")
                .isMultipleChoice(true)
                .options(null) // Option은 나중에 설정
                .build();

        // VoteOption 초기화
        voteOption = VoteOption.builder()
                .vote(mockVote)
                .optionText("Option 1")
                .build();
    }

    @Test
    void 투표_옵션_생성_테스트() {
        // Then
        assertThat(voteOption.getOptionText()).isEqualTo("Option 1");
        assertThat(voteOption.getVote()).isEqualTo(mockVote);
        assertThat(voteOption.getVoters()).isEmpty();
    }

    @Test
    void 투표_등록_테스트() {
        // When
        voteOption.castVote("user1@example.com");
        voteOption.castVote("user2@example.com");

        // Then
        assertThat(voteOption.getVoters()).containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
        assertThat(voteOption.getVoterNumber()).isEqualTo(2);
    }

    @Test
    void 중복_투표_방지_테스트() {
        // When
        voteOption.castVote("user1@example.com");
        voteOption.castVote("user1@example.com"); // 중복 투표

        // Then
        assertThat(voteOption.getVoters()).containsOnly("user1@example.com");
        assertThat(voteOption.getVoterNumber()).isEqualTo(1);
    }

    @Test
    void 투표_옵션_투표_수_검증_테스트() {
        // Given
        voteOption.castVote("user1@example.com");
        voteOption.castVote("user2@example.com");
        voteOption.castVote("user3@example.com");

        // When
        int voterCount = voteOption.getVoterNumber();

        // Then
        assertThat(voterCount).isEqualTo(3);
    }

    @Test
    void 투표와_연결_테스트() {
        // Given
        Vote newVote = Vote.builder()
                .title("New Vote")
                .description("New Description")
                .isMultipleChoice(false)
                .options(null)
                .build();

        // When
        voteOption.setVote(newVote);

        // Then
        assertThat(voteOption.getVote()).isEqualTo(newVote);
    }
}
