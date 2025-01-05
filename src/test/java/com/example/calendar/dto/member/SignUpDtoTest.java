package com.example.calendar.dto.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignUpDtoTest {

    @Test
    void 모든_필드가_유효한_경우_생성_성공_테스트() {
        // Given
        String email = "user@example.com";
        String password = "password123";
        String name = "ValidName";

        // When
        SignUpDto dto = new SignUpDto(email, password, name);

        // Then
        assertThat(dto.email()).isEqualTo(email);
        assertThat(dto.password()).isEqualTo(password);
        assertThat(dto.name()).isEqualTo(name);
    }

    @Test
    void 이메일이_null일_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto(null, "password123", "ValidName"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Email must not be null");
    }

    @Test
    void 잘못된_이메일_형식_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("invalid-email", "password123", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

        assertThatThrownBy(() -> new SignUpDto("missing@domain", "password123", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

        assertThatThrownBy(() -> new SignUpDto("@missinglocal.com", "password123", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    void 이메일_길이가_320자를_초과할_때_예외_테스트() {
        // Given
        String longEmail = "a".repeat(310) + "@example.com";

        // When & Then
        assertThatThrownBy(() -> new SignUpDto(longEmail, "password123", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email must be smaller than 320 characters");
    }

    @Test
    void 이메일에_공백이_포함될_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("email @example.com", "password123", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");

        assertThatThrownBy(() -> new SignUpDto(" email@example.com ", "password123", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    void 패스워드가_null일_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("user@example.com", null, "ValidName"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Password must not be null");
    }

    @Test
    void 패스워드_길이가_5자_미만일_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("user@example.com", "1234", "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be between 5 and 20 characters");
    }

    @Test
    void 패스워드_길이가_20자를_초과할_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("user@example.com", "a".repeat(21), "ValidName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be between 5 and 20 characters");
    }

    @Test
    void 이름이_null일_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("user@example.com", "password123", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name must not be null");
    }

    @Test
    void 이름이_2자_미만일_때_예외_테스트() {
        // When & Then
        assertThatThrownBy(() -> new SignUpDto("user@example.com", "password123", "A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must be at least 2 characters long");
    }
}
