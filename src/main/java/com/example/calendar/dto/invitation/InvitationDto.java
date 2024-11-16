package com.example.calendar.dto.invitation;

import java.util.Optional;

public record InvitationDto (
        String type,
        String senderEmail,
        String senderName,
        String receiverEmail,
        String receiverName,
        Optional<String> teamName
){
    public InvitationDto(String type, String senderEmail, String senderName, String receiverEmail, String receiverName, String teamName) {
        this(type, senderEmail, senderName, receiverEmail, receiverName, Optional.ofNullable(teamName));
    }
}
