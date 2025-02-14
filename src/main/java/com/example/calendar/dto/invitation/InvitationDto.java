package com.example.calendar.dto.invitation;

import java.util.Optional;

public record InvitationDto (
        long id,
        String type,
        String senderEmail,
        String senderName,
        String receiverEmail,
        String receiverName,
        Optional<String> teamName
){
    public InvitationDto(long id, String type, String senderEmail, String senderName, String receiverEmail, String receiverName, String teamName) {
        this(id, type, senderEmail, senderName, receiverEmail, receiverName, Optional.ofNullable(teamName));
    }
}
