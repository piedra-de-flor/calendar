package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.invitation.FriendInvitationDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.service.invitation.InvitationFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FriendFacadeServiceTest {
    @InjectMocks
    private FriendFacadeService friendFacadeService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private InvitationFacadeService invitationFacadeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 친구_생성_성공_테스트() {
        // Given
        String memberEmail = "sender@example.com";
        long friendId = 1L;

        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(friendId)).thenReturn(Optional.of(receiver));
        when(sender.getEmail()).thenReturn(memberEmail);
        when(receiver.getEmail()).thenReturn("receiver@example.com");

        // When
        boolean result = friendFacadeService.createFriend(memberEmail, friendId);

        // Then
        assertThat(result).isTrue();
        verify(invitationFacadeService, times(1)).sendFriendInvitation(eq(memberEmail), any(FriendInvitationDto.class));
    }

    @Test
    void 친구_생성_보낸사람_찾기_실패_테스트() {
        // Given
        String memberEmail = "sender@example.com";
        long friendId = 1L;

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> friendFacadeService.createFriend(memberEmail, friendId))
                .isInstanceOf(NoSuchElementException.class);

        verify(invitationFacadeService, never()).sendFriendInvitation(anyString(), any(FriendInvitationDto.class));
    }

    @Test
    void 친구_생성_받는사람_찾기_실패_테스트() {
        // Given
        String memberEmail = "sender@example.com";
        long friendId = 1L;

        Member sender = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(friendId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> friendFacadeService.createFriend(memberEmail, friendId))
                .isInstanceOf(NoSuchElementException.class);

        verify(invitationFacadeService, never()).sendFriendInvitation(anyString(), any(FriendInvitationDto.class));
    }
}
