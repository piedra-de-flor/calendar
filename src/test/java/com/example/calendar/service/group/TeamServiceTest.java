package com.example.calendar.service.group;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.group.TeamAddFriendDto;
import com.example.calendar.dto.group.TeamCreateDto;
import com.example.calendar.dto.invitation.TeamInvitationDto;
import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.repository.TeamingRepository;
import com.example.calendar.service.invitation.InvitationFacadeService;
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

class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamingRepository teamingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private InvitationFacadeService invitationFacadeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTeam_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        TeamCreateDto createDto = new TeamCreateDto(List.of(2L, 3L), "MyTeam");

        Member member = mock(Member.class);
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getTeamsNames()).thenReturn(List.of());

        Team team = mock(Team.class);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        when(memberRepository.findById(2L)).thenReturn(Optional.of(mock(Member.class)));
        when(memberRepository.findById(3L)).thenReturn(Optional.of(mock(Member.class)));

        // When
        boolean result = teamService.createTeam(memberEmail, createDto);

        // Then
        assertThat(result).isTrue();
        verify(teamRepository, times(1)).save(any(Team.class));
        verify(teamingRepository, times(1)).save(any(Teaming.class));
        verify(invitationFacadeService, times(2)).sendTeamInvitation(eq(memberEmail), any(TeamInvitationDto.class));
    }

    @Test
    void createTeam_중복된_이름_예외_테스트() {
        // Given
        String memberEmail = "user@example.com";
        TeamCreateDto createDto = new TeamCreateDto(List.of(), "Duplicate");

        Member member = mock(Member.class);
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getTeamsNames()).thenReturn(List.of("Duplicate"));

        // When & Then
        assertThatThrownBy(() -> teamService.createTeam(memberEmail, createDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("group name is duplicated");
    }

    @Test
    void addFriend_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        TeamAddFriendDto addFriendDto = new TeamAddFriendDto(1L, "friend@example.com");

        Member sender = mock(Member.class);
        Team team = mock(Team.class);
        Teaming teaming = mock(Teaming.class);
        Member friend = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(sender));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamingRepository.findByMemberAndTeam(sender, team)).thenReturn(Optional.of(teaming));
        when(memberRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(friend));

        // When
        FriendDto result = teamService.addFriend(memberEmail, addFriendDto);

        // Then
        assertThat(result).isNotNull();
        verify(invitationFacadeService, times(1)).sendTeamInvitation(eq(memberEmail), any(TeamInvitationDto.class));
    }

    @Test
    void exitTeam_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long groupId = 1L;

        Member member = mock(Member.class);
        Team team = mock(Team.class);
        Teaming teaming = mock(Teaming.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(teamRepository.findById(groupId)).thenReturn(Optional.of(team));
        when(teamingRepository.findByMemberAndTeam(member, team)).thenReturn(Optional.of(teaming));

        // When
        boolean result = teamService.exitTeam(memberEmail, groupId);

        // Then
        assertThat(result).isTrue();
        verify(teamingRepository, times(1)).delete(teaming);
    }

    @Test
    void deleteTeam_성공_테스트() {
        // Given
        Team emptyTeam = mock(Team.class);
        when(teamRepository.findAllByTeamingsEmpty()).thenReturn(List.of(emptyTeam));

        // When
        teamService.deleteTeam();

        // Then
        verify(teamRepository, times(1)).deleteAll(anyList());
        verify(teamingRepository, times(1)).deleteAllByTeam(emptyTeam);
    }
}
