package com.example.calendar.dto.group;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamCreateDtoTest {

    @Test
    void 이름이_null_일때_예외_테스트() {
        // Given
        List<Long> friends = List.of(1L, 2L, 3L);

        // When & Then
        assertThatThrownBy(() -> new TeamCreateDto(friends, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name must not be null");
    }

    @Test
    void 이름이_비어있을_때_예외_테스트() {
        // Given
        List<Long> friends = List.of(1L, 2L, 3L);

        // When & Then
        assertThatThrownBy(() -> new TeamCreateDto(friends, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group name must be between 1 and 10 characters");
    }

    @Test
    void 이름이_10자를_초과할_때_예외_테스트() {
        // Given
        List<Long> friends = List.of(1L, 2L, 3L);

        // When & Then
        assertThatThrownBy(() -> new TeamCreateDto(friends, "This name is too long"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group name must be between 1 and 10 characters");
    }

    @Test
    void 이름이_앞뒤에_공백이_있을_때_예외_테스트() {
        // Given
        List<Long> friends = List.of(1L, 2L, 3L);

        // When & Then
        assertThatThrownBy(() -> new TeamCreateDto(friends, " Invalid "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group name should not start or end with blank");
    }

    @Test
    void 유효한_이름으로_생성_성공_테스트() {
        // Given
        List<Long> friends = List.of(1L, 2L, 3L);

        // When
        TeamCreateDto dto = new TeamCreateDto(friends, "ValidName");

        // Then
        assertThat(dto.name()).isEqualTo("ValidName");
        assertThat(dto.friends()).containsExactly(1L, 2L, 3L);
    }
}
