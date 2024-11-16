package com.example.calendar.dto.member;

import java.util.Objects;
import java.util.regex.Pattern;

public record SignUpDto(String email, String password, String name) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public SignUpDto {
        Objects.requireNonNull(email, "Email must not be null");
        Objects.requireNonNull(password, "Password must not be null");
        Objects.requireNonNull(name, "Name must not be null");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (password.length() < 5 || password.length() > 20) {
            throw new IllegalArgumentException("Password must be between 5 and 20 characters");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
    }
}
