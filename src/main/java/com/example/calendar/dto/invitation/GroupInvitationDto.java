package com.example.calendar.dto.invitation;

public record GroupInvitationDto (
        String receiverEmail,
        long groupId
) {
}
