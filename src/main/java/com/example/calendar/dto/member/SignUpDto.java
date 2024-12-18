package com.example.calendar.dto.member;

import java.util.Objects;

import static com.example.calendar.domain.vo.EmailPattern.EMAIL_PATTERN;

public record SignUpDto(String email, String password, String name) {
    public SignUpDto {
        Objects.requireNonNull(email, "Email must not be null");
        Objects.requireNonNull(password, "Password must not be null");
        Objects.requireNonNull(name, "Name must not be null");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (email.length() > 320) {
            throw new IllegalArgumentException("Email must be smaller than 320 characters");
        }

        if (email.contains(" ")) {
            throw new IllegalArgumentException("Email should not contain blank");
        }

        if (password.length() < 5 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be between 5 and 20 characters");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
    }
}
