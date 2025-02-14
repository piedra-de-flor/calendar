package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.dto.vote.VoteCreateDto;
import com.example.calendar.repository.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class VoteOptionService {
    private final VoteOptionRepository voteOptionRepository;

    @Transactional
    public List<VoteOption> createVoteOptions(VoteCreateDto createDto){
        List<VoteOption> options = new ArrayList<>();
        for (String text : createDto.voteOptions()) {
            VoteOption option = VoteOption.builder()
                    .optionText(text)
                    .build();

            voteOptionRepository.save(option);
            options.add(option);
        }

        return options;
    }

    public VoteOption readVoteOption(long voteOptionId) {
        return voteOptionRepository.findById(voteOptionId)
                .orElseThrow(() -> new NoSuchElementException("can't find vote option"));
    }
}
