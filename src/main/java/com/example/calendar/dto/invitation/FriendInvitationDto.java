package com.example.calendar.dto.invitation;

import com.example.calendar.dto.exception.RequestValidationException;

import java.util.Objects;

import static com.example.calendar.domain.vo.EmailPattern.EMAIL_PATTERN;

public record FriendInvitationDto (
        String receiverEmail
) {
    public FriendInvitationDto {
        requireNonNull(receiverEmail, "Email must not be null");

        if (!EMAIL_PATTERN.matcher(receiverEmail).matches()) {
            throw new RequestValidationException("Invalid email format");
        }

        if (receiverEmail.length() >= 320) {
            throw new RequestValidationException("Email must be smaller than 320 characters");
        }

        if (receiverEmail.contains(" ")) {
            throw new RequestValidationException("Email should not contain blank");
        }
    }

    private static <T> void requireNonNull(T obj, String message) {
        if (obj == null)
            throw new RequestValidationException(message);
    }
}
