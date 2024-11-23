package com.example.calendar.service.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.entity.vote.VoteOption;
import com.example.calendar.domain.vo.notification.NotificationRedirectUrl;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.member.MemberDto;
import com.example.calendar.dto.vote.*;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.service.notification.NotificationFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class VoteFacadeService {
    private final NotificationFacadeService notificationFacadeService;
    private final VoteService voteService;
    private final VoteOptionService voteOptionService;

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public boolean createVote(String email, VoteCreateDto createDto) {
        Member voteOwner = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Team team = teamRepository.findById(createDto.teamId())
                .orElseThrow(NoSuchElementException::new);

        if (voteOwner.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == team.getId())) {
            throw new IllegalArgumentException("you don't have auth to create the vote about the team");
        }

        boolean response = voteService.createVote(createDto, team, voteOptionService.createVoteOptions(createDto));

        for (Teaming teaming : team.getTeamings()) {
            notificationFacadeService.send(teaming.getMember(), NotificationType.VOTE, notificationFacadeService.voteCreateMessage(team), NotificationRedirectUrl.VOTE_CREATED.getUrl());
        }

        return response;
    }

    public boolean castVote(String voterEmail, long voteId, CastVoteOptionsDto castVoteOptionsDto) {
        return voteService.castVote(voterEmail, voteId, castVoteOptionsDto);
    }

    public VoteDto readVote(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteService.readVote(voteId);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to read the vote about the team");
        }

        return voteService.readVote(vote);
    }

    public VoteOptionDto readVoteOption(String email, long voteId, long voteOptionId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteService.readVote(voteId);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to read the vote about the team");
        }

        VoteOption voteOption = voteOptionService.readVoteOption(voteOptionId);

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

        Vote vote = voteService.readVote(voteId);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to read result about the vote");
        }

        if (vote.isOpen()) {
            throw new IllegalStateException("Vote is still open. Close it to see results.");
        }

        return voteService.getVoteResults(vote);
    }

    @Transactional
    public boolean completeVote(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteService.readVote(voteId);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == vote.getTeam().getId())) {
            throw new IllegalArgumentException("you don't have auth to complete the vote about the team");
        }

        voteService.closeVote(vote);

        for (Teaming teaming : vote.getTeam().getTeamings()) {
            notificationFacadeService.send(teaming.getMember(), NotificationType.VOTE, notificationFacadeService.voteCompleteMessage(vote), NotificationRedirectUrl.VOTE_COMPLETE.getUrl());
        }

        return true;
    }
}
