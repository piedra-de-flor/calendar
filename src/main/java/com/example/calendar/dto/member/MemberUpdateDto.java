package com.example.calendar.dto.member;

import com.example.calendar.dto.exception.RequestValidationException;

import java.util.Objects;

public record MemberUpdateDto(
        String name,
        String password,
        String newPassword
) {
    public MemberUpdateDto {
        requireNonNull(password, "Password must not be null");

        if (newPassword.length() < 5 || newPassword.length() > 20) {
            throw new RequestValidationException("Password must be between 5 and 20 characters");
        }

        if (name.length() < 2) {
            throw new RequestValidationException("Name must be at least 2 characters long");
        }
    }

    private static <T> void requireNonNull(T obj, String message) {
        if (obj == null)
            throw new RequestValidationException(message);
    }
}
