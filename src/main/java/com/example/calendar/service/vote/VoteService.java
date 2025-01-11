package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.domain.vo.vote.VoteStatus;
import com.example.calendar.dto.vote.CastVoteOptionsDto;
import com.example.calendar.dto.vote.VoteCreateDto;
import com.example.calendar.dto.vote.VoteDto;
import com.example.calendar.dto.vote.VoteResultDto;
import com.example.calendar.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class VoteService {
    private final VoteRepository voteRepository;

    @Transactional
    public Vote createVote(VoteCreateDto createDto, Team team, List<VoteOption> options) {
        Vote vote = Vote.builder()
                .team(team)
                .title(createDto.voteTitle())
                .description(createDto.voteDescription())
                .isMultipleChoice(createDto.isMultipleVote())
                .options(options)
                .build();

        return voteRepository.save(vote);
    }

    public Vote readVote(long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("can't find vote"));
    }

    @Transactional
    public boolean castVote(String voterEmail, long voteId, CastVoteOptionsDto castVoteOptionsDto) {
        Vote vote = readVote(voteId);

        if (!vote.isMultipleChoice() && castVoteOptionsDto.optionIds().size() > 1) {
            throw new IllegalArgumentException("This vote only allows single choice.");
        }

        vote.getOptions().forEach(option -> {
            option.getVoters().remove(voterEmail);
        });

        castVoteOptionsDto.optionIds().forEach(optionId -> {
            VoteOption option = vote.getOptions().stream()
                    .filter(opt -> opt.getId() == optionId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid option ID"));

            option.castVote(voterEmail);
        });

        voteRepository.save(vote);
        return true;
    }

    public VoteDto readVote(Vote vote) {
        Map<String, Map<Long, Integer>> optionInfo = new HashMap<>();

        for (VoteOption option : vote.getOptions()) {
            Map<Long, Integer> idAndVoteQuantity = new HashMap<>();
            idAndVoteQuantity.put(option.getId(), option.getVoterNumber());

            optionInfo.put(option.getOptionText(), idAndVoteQuantity);
        }

        return new VoteDto(
                vote.getId(),
                vote.getTitle(),
                vote.getDescription(),
                vote.getStatus(),
                vote.isMultipleChoice(),
                vote.getCreatedAt(),
                vote.getClosedAt(),
                optionInfo
        );
    }

    public VoteResultDto getVoteResults(Vote vote) {
        Map<Long, String> result = new HashMap<>();

        int maxVotes = vote.getOptions().stream()
                .mapToInt(VoteOption::getVoterNumber)
                .max()
                .orElseThrow(() -> new NoSuchElementException("No options available"));

        vote.getOptions().stream()
                .filter(option -> option.getVoterNumber() == maxVotes)
                .forEach(option -> result.put(option.getId(), option.getOptionText()));

        return new VoteResultDto(vote.getId(), result);
    }


    @Transactional
    public void closeVote(Vote vote) {
        vote.close();
        voteRepository.save(vote);
    }

    @Transactional
    public void closeExpiredVotes() {
        List<Vote> openVotes = voteRepository.findAllByStatus(VoteStatus.OPEN);

        for (Vote vote : openVotes) {
            if (vote.getCreatedAt().plusDays(3).isBefore(LocalDateTime.now())) {
                closeVote(vote);
            }
        }

        voteRepository.saveAll(openVotes);
    }

    @Transactional
    public void deleteExpiredVotes() {
        List<Vote> expiredVotes = voteRepository.findAllByStatus(VoteStatus.CLOSED);

        for (Vote vote : expiredVotes) {
            if (vote.getClosedAt().plusDays(7).isBefore(LocalDateTime.now())) {
                voteRepository.delete(vote);
            }
        }
    }

    public List<Vote> readVotes(Team team) {
        return voteRepository.findAllByTeamId(team.getId());
    }
}
