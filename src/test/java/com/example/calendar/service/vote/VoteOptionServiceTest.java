package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.dto.vote.VoteCreateDto;
import com.example.calendar.repository.VoteOptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteOptionServiceTest {

    @InjectMocks
    private VoteOptionService voteOptionService;

    @Mock
    private VoteOptionRepository voteOptionRepository;

    @Test
    void createVoteOptions_성공_테스트() {
        // Given
        VoteCreateDto createDto = new VoteCreateDto(1L, "Test Vote", "Description", true, List.of("Option1", "Option2"));

        VoteOption option1 = VoteOption.builder().optionText("Option1").build();
        VoteOption option2 = VoteOption.builder().optionText("Option2").build();

        when(voteOptionRepository.save(any(VoteOption.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 객체 그대로 반환

        // When
        List<VoteOption> result = voteOptionService.createVoteOptions(createDto);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOptionText()).isEqualTo("Option1");
        assertThat(result.get(1).getOptionText()).isEqualTo("Option2");

        verify(voteOptionRepository, times(2)).save(any(VoteOption.class)); // 저장 호출 검증
    }

    @Test
    void readVoteOption_성공_테스트() {
        // Given
        long voteOptionId = 1L;
        VoteOption expectedOption = VoteOption.builder().optionText("Option1").build();

        when(voteOptionRepository.findById(voteOptionId)).thenReturn(Optional.of(expectedOption));

        // When
        VoteOption result = voteOptionService.readVoteOption(voteOptionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOptionText()).isEqualTo("Option1");

        verify(voteOptionRepository, times(1)).findById(voteOptionId); // 조회 호출 검증
    }

    @Test
    void readVoteOption_예외_테스트() {
        // Given
        long voteOptionId = 1L;

        when(voteOptionRepository.findById(voteOptionId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteOptionService.readVoteOption(voteOptionId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("can't find vote option");

        verify(voteOptionRepository, times(1)).findById(voteOptionId); // 조회 호출 검증
    }
}
