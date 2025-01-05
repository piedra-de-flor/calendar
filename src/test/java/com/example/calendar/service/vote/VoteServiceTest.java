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
    void createVote_성공_테스트() {
        // Given
        Team team = mock(Team.class);
        List<VoteOption> options = List.of(
                VoteOption.builder().optionText("Option1").build(),
                VoteOption.builder().optionText("Option2").build()
        );
        VoteCreateDto createDto = new VoteCreateDto(1L, "Test Vote", "Test Description", true, List.of("Option1", "Option2"));
        Vote expectedVote = mock(Vote.class);

        when(voteRepository.save(any(Vote.class))).thenReturn(expectedVote);

        // When
        Vote result = voteService.createVote(createDto, team, options);

        // Then
        assertThat(result).isEqualTo(expectedVote);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void readVote_성공_테스트() {
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
    void readVote_예외_테스트() {
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
    void castVote_성공_테스트() {
        // Given
        String voterEmail = "voter@example.com";
        long voteId = 1L;
        CastVoteOptionsDto castVoteOptionsDto = new CastVoteOptionsDto(List.of(1L));
        Vote vote = mock(Vote.class);
        VoteOption option = mock(VoteOption.class);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));
        when(vote.getOptions()).thenReturn(List.of(option));
        when(option.getId()).thenReturn(1L);

        // When
        boolean result = voteService.castVote(voterEmail, voteId, castVoteOptionsDto);

        // Then
        assertThat(result).isTrue();
        verify(voteRepository, times(1)).save(vote);
    }

    @Test
    void castVote_예외_테스트_잘못된옵션ID() {
        // Given
        String voterEmail = "voter@example.com";
        long voteId = 1L;
        CastVoteOptionsDto castVoteOptionsDto = new CastVoteOptionsDto(List.of(99L)); // 잘못된 옵션 ID
        Vote vote = mock(Vote.class);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));
        when(vote.getOptions()).thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voterEmail, voteId, castVoteOptionsDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid option ID");
    }

    @Test
    void closeVote_성공_테스트() {
        // Given
        Vote vote = mock(Vote.class);

        // When
        voteService.closeVote(vote);

        // Then
        verify(vote, times(1)).close();
        verify(voteRepository, times(1)).save(vote);
    }

    @Test
    void closeExpiredVotes_성공_테스트() {
        // Given
        Vote vote = mock(Vote.class);
        when(vote.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(4));
        when(voteRepository.findAllByStatus(VoteStatus.OPEN)).thenReturn(List.of(vote));

        // When
        voteService.closeExpiredVotes();

        // Then
        verify(voteRepository, times(1)).findAllByStatus(VoteStatus.OPEN);
        verify(vote, times(1)).close();
        verify(voteRepository, times(1)).saveAll(anyList());
    }

    @Test
    void deleteExpiredVotes_성공_테스트() {
        // Given
        Vote vote = mock(Vote.class);
        when(vote.getClosedAt()).thenReturn(LocalDateTime.now().minusDays(8));
        when(voteRepository.findAllByStatus(VoteStatus.CLOSED)).thenReturn(List.of(vote));

        // When
        voteService.deleteExpiredVotes();

        // Then
        verify(voteRepository, times(1)).findAllByStatus(VoteStatus.CLOSED);
        verify(voteRepository, times(1)).delete(vote);
    }
}
