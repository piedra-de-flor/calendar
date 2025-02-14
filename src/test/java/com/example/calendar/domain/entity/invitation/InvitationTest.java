package com.example.calendar.domain.entity.invitation;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InvitationTest {
    private Member mockSender = mock(Member.class);;
    private Member mockReceiver = mock(Member.class);;
    private Invitation invitation = Invitation.builder()
            .sender(mockSender)
            .receiver(mockReceiver)
            .build();

    @Test
    void 초대_거절_테스트() {
        invitation.denied();
        assertThat(invitation.getState()).isEqualTo(InvitationState.DENIED);
    }

    @Test
    void 초대_발신자_확인_테스트() {
        Member anotherMember = mock(Member.class);
        when(mockSender.getId()).thenReturn(1L);
        when(mockReceiver.getId()).thenReturn(2L);
        when(anotherMember.getId()).thenReturn(3L);

        assertThat(invitation.isSentBy(mockSender)).isTrue();
        assertThat(invitation.isSentBy(anotherMember)).isFalse();

        verify(mockSender, atLeastOnce()).getId();
        verify(anotherMember, atLeastOnce()).getId();
    }
}
