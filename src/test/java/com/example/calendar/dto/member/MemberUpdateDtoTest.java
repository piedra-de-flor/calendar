package com.example.calendar.dto.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberUpdateDtoTest {

    @Test
    void 이름과_패스워드가_모두_유효한_경우_생성_테스트() {
        // Given
        String name = "ValidName";
        String password = "currentPassword";
        String newPassword = "newPassword";

        // When
        MemberUpdateDto dto = new MemberUpdateDto(name, password, newPassword);

        // Then
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.password()).isEqualTo(password);
        assertThat(dto.newPassword()).isEqualTo(newPassword);
    }

    @Test
    void 현재_패스워드가_null일_경우_예외_테스트() {
        // Given
        String name = "ValidName";
        String newPassword = "newPassword";

        // When & Then
        assertThatThrownBy(() -> new MemberUpdateDto(name, null, newPassword))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Password must not be null");
    }

    @Test
    void 새_패스워드가_5자_미만일_경우_예외_테스트() {
        // Given
        String name = "ValidName";
        String password = "currentPassword";
        String newPassword = "1234"; // 5자 미만

        // When & Then
        assertThatThrownBy(() -> new MemberUpdateDto(name, password, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be between 5 and 20 characters");
    }

    @Test
    void 새_패스워드가_20자를_초과할_경우_예외_테스트() {
        // Given
        String name = "ValidName";
        String password = "currentPassword";
        String newPassword = "a".repeat(21); // 21자

        // When & Then
        assertThatThrownBy(() -> new MemberUpdateDto(name, password, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be between 5 and 20 characters");
    }

    @Test
    void 이름이_2자_미만일_경우_예외_테스트() {
        // Given
        String name = "A"; // 1자
        String password = "currentPassword";
        String newPassword = "newPassword";

        // When & Then
        assertThatThrownBy(() -> new MemberUpdateDto(name, password, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must be at least 2 characters long");
    }
}
