package com.example.calendar.dto.invitation;

public record TeamInvitationDto(
        String receiverEmail,
        long teamId
) {
}
