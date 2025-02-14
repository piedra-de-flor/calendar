package com.example.calendar.service.invitation;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationState;
import com.example.calendar.domain.vo.notification.NotificationRedirectUrl;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.invitation.FriendInvitationDto;
import com.example.calendar.dto.invitation.InvitationDto;
import com.example.calendar.dto.invitation.TeamInvitationDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.service.notification.NotificationFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class InvitationFacadeServiceTest {
    @InjectMocks
    private InvitationFacadeService invitationFacadeService;
    @Mock
    private InvitationService invitationService;
    @Mock
    private NotificationFacadeService notificationService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 친구_초대_성공_테스트() {
        // Given
        String memberEmail = "sender@example.com";
        FriendInvitationDto invitationDto = new FriendInvitationDto("receiver@example.com");

        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);
        Invitation invitation = mock(Invitation.class);

        when(memberRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(memberRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(sender.isFriend(receiver)).thenReturn(false);
        when(invitationService.createFriendInvitation(sender, receiver)).thenReturn(invitation);

        // Mock 메시지 반환 설정
        when(notificationService.inviteFriendMessage(sender)).thenReturn("Friend invitation sent");

        // When
        boolean result = invitationFacadeService.sendFriendInvitation(memberEmail, invitationDto);

        // Then
        assertThat(result).isTrue();
        verify(notificationService, times(1)).send(eq(receiver), eq(NotificationType.INVITATION), eq("Friend invitation sent"), eq(NotificationRedirectUrl.INVITATION_FRIEND.getUrl()));
    }

    @Test
    void 친구_초대_이미_친구거나_중복_초대_예외_테스트() {
        // Given
        String memberEmail = "sender@example.com";
        FriendInvitationDto invitationDto = new FriendInvitationDto("receiver@example.com");

        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);

        when(memberRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(memberRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(sender.isFriend(receiver)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> invitationFacadeService.sendFriendInvitation(memberEmail, invitationDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("send Friend invitation error");
    }

    @Test
    void 팀_초대_성공_테스트() {
        // Given
        String memberEmail = "sender@example.com";
        TeamInvitationDto invitationDto = new TeamInvitationDto("receiver@example.com", 1L);

        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);
        Team team = mock(Team.class);
        Invitation invitation = mock(Invitation.class);

        when(memberRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(memberRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(sender.inTheTeam(1L)).thenReturn(true);
        when(receiver.inTheTeam(1L)).thenReturn(false);
        when(invitationService.createTeamInvitation(sender, receiver, team)).thenReturn(invitation);

        // Mock notificationService.inviteTeamMessage 반환값 설정
        when(notificationService.inviteTeamMessage(sender, team)).thenReturn("Team invitation message");

        // When
        boolean result = invitationFacadeService.sendTeamInvitation(memberEmail, invitationDto);

        // Then
        assertThat(result).isTrue();
        verify(notificationService, times(1)).send(eq(receiver), eq(NotificationType.INVITATION), eq("Team invitation message"), eq(NotificationRedirectUrl.INVITATION_TEAM.getUrl()));
    }

    @Test
    void 초대_수락_성공_테스트() {
        // Given
        long invitationId = 1L;
        Invitation invitation = mock(Invitation.class);
        Member receiver = mock(Member.class);
        Member sender = mock(Member.class);

        when(invitationService.readInvitation(invitationId)).thenReturn(invitation);
        when(invitation.getReceiver()).thenReturn(receiver);
        when(invitation.getSender()).thenReturn(sender);

        // Mock 메시지 반환 설정
        when(notificationService.acceptInvitation(receiver)).thenReturn("Invitation accepted");

        // When
        boolean result = invitationFacadeService.acceptInvitation(invitationId);

        // Then
        assertThat(result).isTrue();
        verify(invitationService, times(1)).deleteInvitation(invitationId);
        verify(notificationService, times(1)).send(eq(sender), eq(NotificationType.INVITATION), eq("Invitation accepted"), eq(NotificationRedirectUrl.INVITATION_TEAM.getUrl()));
    }

    @Test
    void 초대_거절_성공_테스트() {
        // Given
        long invitationId = 1L;
        Invitation invitation = mock(Invitation.class);

        when(invitationService.readInvitation(invitationId)).thenReturn(invitation);

        // When
        boolean result = invitationFacadeService.deniedInvitation(invitationId);

        // Then
        assertThat(result).isTrue();
        verify(invitation, times(1)).denied();
    }

    @Test
    void 받은_초대_조회_테스트() {
        // Given
        String memberEmail = "receiver@example.com";
        Member receiver = mock(Member.class);
        Invitation invitation = mock(Invitation.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(receiver));
        when(invitationService.realAllReceiveInvitations(receiver)).thenReturn(List.of(invitation));
        when(invitation.getState()).thenReturn(InvitationState.NOT_DECIDE);

        // When
        List<InvitationDto> result = invitationFacadeService.realAllReceiveFriendInvitations(memberEmail);

        // Then
        assertThat(result).isNotNull();
        verify(invitationService, times(1)).realAllReceiveInvitations(receiver);
    }
}
