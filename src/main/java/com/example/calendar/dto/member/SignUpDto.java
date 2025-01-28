package com.example.calendar.dto.member;

import com.example.calendar.dto.exception.RequestValidationException;

import java.util.Objects;

import static com.example.calendar.domain.vo.EmailPattern.EMAIL_PATTERN;

public record SignUpDto(String email, String password, String name) {
    public SignUpDto {
        requireNonNull(email, "Email must not be null");
        requireNonNull(password, "Password must not be null");
        requireNonNull(name, "Name must not be null");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RequestValidationException("Invalid email format");
        }

        if (email.length() > 320) {
            throw new RequestValidationException("Email must be smaller than 320 characters");
        }

        if (email.contains(" ")) {
            throw new RequestValidationException("Email should not contain blank");
        }

        if (password.length() < 5 || password.length() > 20) {
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
