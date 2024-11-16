package com.example.calendar.dto.member;

import java.util.Objects;

public record MemberUpdateDto(
        String name,
        String password,
        String newPassword
) {
    public MemberUpdateDto {
        Objects.requireNonNull(password, "Password must not be null");

        if (newPassword.length() < 5 || newPassword.length() > 20) {
            throw new IllegalArgumentException("Password must be between 5 and 20 characters");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
    }
}
