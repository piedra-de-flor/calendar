package com.example.calendar.dto.invitation;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InvitationDtoTest {
    @Test
    void 팀_이름이_null인_경우_Optional_빈값_처리_테스트() {
        // Given
        long id = 1L;
        String type = "FRIEND";
        String senderEmail = "sender@example.com";
        String senderName = "Sender";
        String receiverEmail = "receiver@example.com";
        String receiverName = "Receiver";
        String teamName = null;

        // When
        InvitationDto dto = new InvitationDto(id, type, senderEmail, senderName, receiverEmail, receiverName, teamName);

        // Then
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.type()).isEqualTo(type);
        assertThat(dto.senderEmail()).isEqualTo(senderEmail);
        assertThat(dto.senderName()).isEqualTo(senderName);
        assertThat(dto.receiverEmail()).isEqualTo(receiverEmail);
        assertThat(dto.receiverName()).isEqualTo(receiverName);
        assertThat(dto.teamName()).isEqualTo(Optional.empty());
    }

    @Test
    void 팀_이름이_비어있는_경우_Optional_빈값_처리_테스트() {
        // Given
        long id = 1L;
        String type = "FRIEND";
        String senderEmail = "sender@example.com";
        String senderName = "Sender";
        String receiverEmail = "receiver@example.com";
        String receiverName = "Receiver";
        String teamName = "";

        // When
        InvitationDto dto = new InvitationDto(id, type, senderEmail, senderName, receiverEmail, receiverName, teamName);

        // Then
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.type()).isEqualTo(type);
        assertThat(dto.senderEmail()).isEqualTo(senderEmail);
        assertThat(dto.senderName()).isEqualTo(senderName);
        assertThat(dto.receiverEmail()).isEqualTo(receiverEmail);
        assertThat(dto.receiverName()).isEqualTo(receiverName);
        assertThat(dto.teamName()).isEqualTo(Optional.of(""));
    }
}
