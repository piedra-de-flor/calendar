package com.example.calendar.dto.invitation;

import java.util.Objects;

import static com.example.calendar.domain.vo.EmailPattern.EMAIL_PATTERN;

public record FriendInvitationDto (
        String receiverEmail
) {
    public FriendInvitationDto {
        Objects.requireNonNull(receiverEmail, "Email must not be null");

        if (!EMAIL_PATTERN.matcher(receiverEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (receiverEmail.length() < 320) {
            throw new IllegalArgumentException("Email must be smaller than 320 characters");
        }

        if (receiverEmail.contains(" ")) {
            throw new IllegalArgumentException("Email should not contain blank");
        }
    }
}
