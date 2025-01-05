package com.example.calendar.service.invitation;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.invitation.FriendInvitation;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.invitation.TeamInvitation;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.repository.InvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class InvitationServiceTest {

    @InjectMocks
    private InvitationService invitationService;

    @Mock
    private InvitationRepository invitationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 친구_초대_생성_성공_테스트() {
        // Given
        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);
        Invitation friendInvitation = mock(FriendInvitation.class);

        when(invitationRepository.save(any(FriendInvitation.class))).thenReturn((FriendInvitation) friendInvitation);

        // When
        Invitation result = invitationService.createFriendInvitation(sender, receiver);

        // Then
        assertThat(result).isNotNull();
        verify(invitationRepository, times(1)).save(any(FriendInvitation.class));
    }

    @Test
    void 팀_초대_생성_성공_테스트() {
        // Given
        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);
        Team team = mock(Team.class);
        Invitation teamInvitation = mock(TeamInvitation.class);

        when(invitationRepository.save(any(TeamInvitation.class))).thenReturn((TeamInvitation) teamInvitation);

        // When
        Invitation result = invitationService.createTeamInvitation(sender, receiver, team);

        // Then
        assertThat(result).isNotNull();
        verify(invitationRepository, times(1)).save(any(TeamInvitation.class));
    }

    @Test
    void 보낸_초대_전체_조회_성공_테스트() {
        // Given
        Member member = mock(Member.class);
        Invitation invitation = mock(Invitation.class);

        when(member.getSentInvitations()).thenReturn(List.of(invitation));

        // When
        List<Invitation> result = invitationService.realAllSendInvitations(member);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains(invitation);
        verify(member, times(1)).getSentInvitations();
    }

    @Test
    void 받은_초대_전체_조회_성공_테스트() {
        // Given
        Member member = mock(Member.class);
        Invitation invitation = mock(Invitation.class);

        when(member.getReceivedInvitations()).thenReturn(List.of(invitation));

        // When
        List<Invitation> result = invitationService.realAllReceiveInvitations(member);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains(invitation);
        verify(member, times(1)).getReceivedInvitations();
    }

    @Test
    void 초대_단일_조회_성공_테스트() {
        // Given
        long invitationId = 1L;
        Invitation invitation = mock(Invitation.class);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));

        // When
        Invitation result = invitationService.readInvitation(invitationId);

        // Then
        assertThat(result).isNotNull();
        verify(invitationRepository, times(1)).findById(invitationId);
    }

    @Test
    void 초대_단일_조회_실패_테스트() {
        // Given
        long invitationId = 1L;

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> invitationService.readInvitation(invitationId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 초대_취소_성공_테스트() {
        // Given
        Member member = mock(Member.class);
        Invitation invitation = mock(Invitation.class);

        when(invitation.isSentBy(member)).thenReturn(true);

        // When
        boolean result = invitationService.cancelInvitation(member, invitation);

        // Then
        assertThat(result).isTrue();
        verify(invitation, times(1)).isSentBy(member);
        verify(invitationRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void 초대_취소_실패_테스트() {
        // Given
        Member member = mock(Member.class);
        Invitation invitation = mock(Invitation.class);

        when(invitation.isSentBy(member)).thenReturn(false);

        // When
        boolean result = invitationService.cancelInvitation(member, invitation);

        // Then
        assertThat(result).isTrue(); // cancelInvitation의 반환값은 항상 true
        verify(invitation, times(1)).isSentBy(member);
        verify(invitationRepository, never()).deleteById(anyLong());
    }

    @Test
    void 초대_삭제_성공_테스트() {
        // Given
        long invitationId = 1L;

        // When
        invitationService.deleteInvitation(invitationId);

        // Then
        verify(invitationRepository, times(1)).deleteById(invitationId);
    }
}
