package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.dto.member.MemberDto;
import com.example.calendar.dto.vote.*;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.repository.VoteOptionRepository;
import com.example.calendar.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public boolean createVote(String email, VoteCreateDto createDto) {
        Member voteOwner = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Team team = teamRepository.findById(createDto.teamId())
                .orElseThrow(NoSuchElementException::new);

        if (voteOwner.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == team.getId())) {
            throw new IllegalArgumentException("you don't have auth to create the vote about the team");
        }

        List<VoteOption> options = new ArrayList<>();
        for (String text : createDto.voteOptions()) {
            VoteOption option = VoteOption.builder()
                    .optionText(text)
                    .build();

            voteOptionRepository.save(option);
            options.add(option);
        }

        Vote vote = Vote.builder()
                .team(team)
                .title(createDto.VoteTitle())
                .description(createDto.VoteDescription())
                .isMultipleChoice(createDto.isMultipleVote())
                .options(options)
                .build();

        voteRepository.save(vote);
        return true;
    }

    @Transactional
    public boolean castVote(String voterEmail, long voteId, CastVoteOptionsDto castVoteOptionsDto) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("Vote not found"));

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

            option.getVoters().add(voterEmail);
            option.castVote(voterEmail);
        });

        voteRepository.save(vote);
        return true;
    }

    public VoteDto readVote(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to read the vote about the team");
        }

        Map<String, Map<Long, Integer>> optionInfo = new HashMap<>();

        for (VoteOption option : vote.getOptions()) {
            Map<Long, Integer> idAndVoteQuantity = new HashMap<>();
            idAndVoteQuantity.put(option.getId(), option.getVoterNumber());

            optionInfo.put(option.getOptionText(), idAndVoteQuantity);
        }

        return new VoteDto(
                voteId,
                vote.getTitle(),
                vote.getDescription(),
                vote.getStatus(),
                optionInfo
        );
    }

    public VoteOptionDto readVoteOption(String email, long voteId, long voteOptionId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to read the vote about the team");
        }

        VoteOption voteOption = voteOptionRepository.findById(voteOptionId)
                .orElseThrow(NoSuchElementException::new);

        if (!vote.getOptions().contains(voteOption)) {
            throw new IllegalArgumentException("Invalid Option ID");
        }

        List<MemberDto> voters = new ArrayList<>();
        for (String voterEmail : voteOption.getVoters()) {
            Member voter = memberRepository.findByEmail(voterEmail)
                    .orElseThrow(NoSuchElementException::new);

            MemberDto memberDto = new MemberDto(voter.getName(), voter.getEmail());
            voters.add(memberDto);
        }

        return new VoteOptionDto(voteOption.getOptionText(), voters);
    }

    public VoteResultDto getVoteResults(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to read result about the vote");
        }

        if (vote.isOpen()) {
            throw new IllegalStateException("Vote is still open. Close it to see results.");
        }

        Map<Long, String> result = new HashMap<>();

        VoteOption winner = vote.getOptions().stream()
                .max(Comparator.comparingInt(VoteOption::getVoterNumber))
                .orElseThrow(() -> new NoSuchElementException("No options available"));

        result.put(winner.getId(), winner.getOptionText());

        return new VoteResultDto(vote.getId(), result);
    }

    @Transactional
    public boolean completeVote(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to complete the vote about the team");
        }

        vote.close();
        voteRepository.save(vote);

        return true;
    }
}
