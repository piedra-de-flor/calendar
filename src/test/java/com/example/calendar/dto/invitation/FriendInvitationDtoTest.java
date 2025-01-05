package com.example.calendar.dto.invitation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FriendInvitationDtoTest {

    @Test
    void 이메일이_null_일때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new FriendInvitationDto(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Email must not be null");
    }

    @Test
    void 잘못된_이메일_형식_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new FriendInvitationDto("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

        assertThatThrownBy(() -> new FriendInvitationDto("missing@domain"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

        assertThatThrownBy(() -> new FriendInvitationDto("@missinglocal.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    void 이메일_길이가_320자를_초과할_때_예외_테스트() {
        // Given
        String longEmail = "a".repeat(310) + "@example.com";

        // When & Then
        assertThatThrownBy(() -> new FriendInvitationDto(longEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email must be smaller than 320 characters");
    }

    @Test
    void 이메일에_공백이_포함될_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new FriendInvitationDto("email @example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

        assertThatThrownBy(() -> new FriendInvitationDto(" email@example.com "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    void 유효한_이메일로_생성_성공_테스트() {
        // Given
        String validEmail = "user@example.com";

        // When
        FriendInvitationDto dto = new FriendInvitationDto(validEmail);

        // Then
        assertThat(dto.receiverEmail()).isEqualTo(validEmail);
    }
}
