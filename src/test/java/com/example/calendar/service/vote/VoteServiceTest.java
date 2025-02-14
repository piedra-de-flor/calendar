package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.domain.vo.vote.VoteStatus;
import com.example.calendar.dto.vote.CastVoteOptionsDto;
import com.example.calendar.dto.vote.VoteCreateDto;
import com.example.calendar.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @InjectMocks
    private VoteService voteService;

    @Mock
    private VoteRepository voteRepository;

    @Test
    void 투표_생성_성공_테스트() {
        Team team = mock(Team.class);
        List<VoteOption> options = List.of(
                VoteOption.builder().optionText("Option1").build(),
                VoteOption.builder().optionText("Option2").build()
        );
        VoteCreateDto createDto = new VoteCreateDto(1L, "Test Vote", "Test Description", true, List.of("Option1", "Option2"));
        Vote expectedVote = mock(Vote.class);

        when(voteRepository.save(any(Vote.class))).thenReturn(expectedVote);

        Vote result = voteService.createVote(createDto, team, options);

        assertThat(result).isEqualTo(expectedVote);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void 투표_조회_성공_테스트() {
        // Given
        long voteId = 1L;
        Vote expectedVote = mock(Vote.class);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(expectedVote));

        // When
        Vote result = voteService.readVote(voteId);

        // Then
        assertThat(result).isEqualTo(expectedVote);
        verify(voteRepository, times(1)).findById(voteId);
    }

    @Test
    void 투표_조회_예외_테스트() {
        // Given
        long voteId = 1L;

        when(voteRepository.findById(voteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.readVote(voteId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("can't find vote");

        verify(voteRepository, times(1)).findById(voteId);
    }

    @Test
    void 투표_성공_테스트() {
        String voterEmail = "voter@example.com";
        long voteId = 1L;
        CastVoteOptionsDto castVoteOptionsDto = new CastVoteOptionsDto(List.of(1L));
        Vote vote = mock(Vote.class);
        VoteOption option = mock(VoteOption.class);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));
        when(vote.getOptions()).thenReturn(List.of(option));
        when(option.getId()).thenReturn(1L);

        boolean result = voteService.castVote(voterEmail, voteId, castVoteOptionsDto);

        assertThat(result).isTrue();
        verify(voteRepository, times(1)).save(vote);
    }

    @Test
    void 투표_예외_테스트_잘못된_옵션_ID() {
        String voterEmail = "voter@example.com";
        long voteId = 1L;
        CastVoteOptionsDto castVoteOptionsDto = new CastVoteOptionsDto(List.of(99L));
        Vote vote = mock(Vote.class);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));
        when(vote.getOptions()).thenReturn(List.of());

        assertThatThrownBy(() -> voteService.castVote(voterEmail, voteId, castVoteOptionsDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid option ID");
    }

    @Test
    void 투표_종료_성공_테스트() {
        Vote vote = mock(Vote.class);

        voteService.closeVote(vote);

        verify(vote, times(1)).close();
        verify(voteRepository, times(1)).save(vote);
    }

    @Test
    void 만료된_투표_종료_성공_테스트() {
        Vote vote = mock(Vote.class);
        when(vote.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(4));
        when(voteRepository.findAllByStatus(VoteStatus.OPEN)).thenReturn(List.of(vote));

        voteService.closeExpiredVotes();

        verify(voteRepository, times(1)).findAllByStatus(VoteStatus.OPEN);
        verify(vote, times(1)).close();
        verify(voteRepository, times(1)).saveAll(anyList());
    }

    @Test
    void 만료된_투표_삭제_성공_테스트() {
        Vote vote = mock(Vote.class);
        when(vote.getClosedAt()).thenReturn(LocalDateTime.now().minusDays(8));
        when(voteRepository.findAllByStatus(VoteStatus.CLOSED)).thenReturn(List.of(vote));

        voteService.deleteExpiredVotes();

        verify(voteRepository, times(1)).findAllByStatus(VoteStatus.CLOSED);
        verify(voteRepository, times(1)).delete(vote);
    }
}
