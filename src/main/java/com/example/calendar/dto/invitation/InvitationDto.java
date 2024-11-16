package com.example.calendar.dto.invitation;

import java.util.Optional;

public record InvitationDto (
        String type,
        String senderEmail,
        String senderName,
        String receiverEmail,
        String receiverName,
        Optional<String> GroupName
){
    public InvitationDto(String type, String senderEmail, String senderName, String receiverEmail, String receiverName, String groupName) {
        this(type, senderEmail, senderName, receiverEmail, receiverName, Optional.ofNullable(groupName));
    }
}
