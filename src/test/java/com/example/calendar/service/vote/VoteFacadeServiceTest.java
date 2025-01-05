package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.domain.vo.notification.NotificationRedirectUrl;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.vote.CastVoteOptionsDto;
import com.example.calendar.dto.vote.VoteCreateDto;
import com.example.calendar.dto.vote.VoteDto;
import com.example.calendar.dto.vote.VoteResultDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.service.notification.NotificationFacadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteFacadeServiceTest {
    @InjectMocks
    private VoteFacadeService voteFacadeService;
    @Mock
    private NotificationFacadeService notificationFacadeService;
    @Mock
    private VoteService voteService;
    @Mock
    private VoteOptionService voteOptionService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TeamRepository teamRepository;

    @Test
    void 투표_생성_성공_테스트() {
        // Given
        String email = "creator@example.com";
        VoteCreateDto createDto = new VoteCreateDto(1L, "Test Vote", "Description", true, List.of("Option1", "Option2"));

        Member creator = mock(Member.class);
        Team team = mock(Team.class);
        Teaming teaming = mock(Teaming.class);
        Vote vote = mock(Vote.class);
        VoteOption option1 = mock(VoteOption.class);
        VoteOption option2 = mock(VoteOption.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(creator));
        when(teamRepository.findById(createDto.teamId())).thenReturn(Optional.of(team));
        when(team.getTeamings()).thenReturn(Set.of(teaming));
        when(teaming.getMember()).thenReturn(creator);
        when(creator.getTeamings()).thenReturn(List.of(teaming));
        when(teaming.getTeam()).thenReturn(team);
        when(voteOptionService.createVoteOptions(createDto)).thenReturn(List.of(option1, option2));
        when(voteService.createVote(createDto, team, List.of(option1, option2))).thenReturn(vote);

        String redirectUrl = NotificationRedirectUrl.VOTE_CREATED.getUrl();
        when(notificationFacadeService.voteCreateMessage(team)).thenReturn("Vote created successfully");

        // When
        boolean result = voteFacadeService.createVote(email, createDto);

        // Then
        assertThat(result).isTrue();
        verify(notificationFacadeService, atLeastOnce()).send(any(Member.class), eq(NotificationType.VOTE), eq("Vote created successfully"), eq(redirectUrl));
        verify(voteOptionService, times(1)).createVoteOptions(createDto);
        verify(voteService, times(1)).createVote(createDto, team, List.of(option1, option2));
    }

    @Test
    void 투표_성공_테스트() {
        // Given
        String email = "voter@example.com";
        long voteId = 1L;
        CastVoteOptionsDto castVoteOptionsDto = new CastVoteOptionsDto(List.of(1L));

        when(voteService.castVote(email, voteId, castVoteOptionsDto)).thenReturn(true);

        // When
        boolean result = voteFacadeService.castVote(email, voteId, castVoteOptionsDto);

        // Then
        assertThat(result).isTrue();
        verify(voteService).castVote(email, voteId, castVoteOptionsDto);
    }

    @Test
    void 투표_조회_성공_테스트() {
        // Given
        String email = "member@example.com";
        long voteId = 1L;

        Member member = mock(Member.class);
        Vote vote = mock(Vote.class);
        Team team = mock(Team.class);
        Teaming teaming = mock(Teaming.class);
        VoteDto voteDto = mock(VoteDto.class);

        // Mock 설정
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(voteService.readVote(voteId)).thenReturn(vote);
        when(vote.getTeam()).thenReturn(team);
        when(member.getTeamings()).thenReturn(List.of(teaming));
        when(teaming.getTeam()).thenReturn(team);
        when(voteService.readVote(vote)).thenReturn(voteDto);

        // When
        VoteDto result = voteFacadeService.readVote(email, voteId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(voteDto);
        verify(voteService, times(1)).readVote(voteId);
        verify(voteService, times(1)).readVote(vote);
    }


    @Test
    void 투표_결과_가져오기_성공_테스트() {
        // Given
        String email = "member@example.com";
        long voteId = 1L;

        Member member = mock(Member.class);
        Vote vote = mock(Vote.class);
        Team team = mock(Team.class);
        VoteResultDto voteResultDto = mock(VoteResultDto.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(voteService.readVote(voteId)).thenReturn(vote);
        when(vote.getTeam()).thenReturn(team);
        when(member.getTeamings()).thenReturn(List.of(mock(Teaming.class)));
        when(member.getTeamings().get(0).getTeam()).thenReturn(team);
        when(voteService.getVoteResults(vote)).thenReturn(voteResultDto);
        when(vote.isOpen()).thenReturn(false);

        // When
        VoteResultDto result = voteFacadeService.getVoteResults(email, voteId);

        // Then
        assertThat(result).isNotNull();
        verify(voteService).getVoteResults(vote);
    }

    @Test
    void 투표_완료_성공_테스트() {
        // Given
        String email = "creator@example.com";
        long voteId = 1L;

        Member creator = mock(Member.class);
        Vote vote = mock(Vote.class);
        Team team = mock(Team.class);
        Teaming teaming = mock(Teaming.class);

        String redirectUrl = NotificationRedirectUrl.VOTE_COMPLETE.getUrl();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(creator));
        when(voteService.readVote(voteId)).thenReturn(vote);
        when(vote.getTeam()).thenReturn(team);
        when(team.getTeamings()).thenReturn(Set.of(teaming));
        when(teaming.getMember()).thenReturn(creator);
        when(creator.getTeamings()).thenReturn(List.of(teaming));
        when(teaming.getTeam()).thenReturn(team);
        when(notificationFacadeService.voteCompleteMessage(vote)).thenReturn("Vote completed for test");

        // When
        boolean result = voteFacadeService.completeVote(email, voteId);

        // Then
        assertThat(result).isTrue();
        verify(voteService, times(1)).closeVote(vote);
        verify(notificationFacadeService, times(1)).send(eq(creator), eq(NotificationType.VOTE), eq("Vote completed for test"), eq(redirectUrl));
    }
}
