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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        List<VoteOption> options = voteOptionService.createVoteOptions(createDto);
        Vote vote = voteService.createVote(createDto, team, options);

        for (VoteOption option : options) {
            option.setVote(vote);
        }

        for (Teaming teaming : team.getTeamings()) {
            notificationFacadeService.send(teaming.getMember(), NotificationType.VOTE, notificationFacadeService.voteCreateMessage(team), NotificationRedirectUrl.VOTE_CREATED.getUrl());
        }

        return true;
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

    public List<VoteDto> readVotes(String email, long teamId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getTeamings().stream()
                .noneMatch(teaming -> teaming.getTeam().getId() == teamId)) {
            throw new IllegalArgumentException("you don't have auth to read the vote about the team");
        }

        List<Vote> votes = voteService.readVotes(team);
        return votes.stream()
                .map(voteService::readVote)
                .collect(Collectors.toList());
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

    public boolean isCasted(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteService.readVote(voteId);

        for (VoteOption option : vote.getOptions()) {
            if (option.isCasted(member.getEmail())) {
                return true;
            }
        }

        return false;
    }

    public List<Long> readOptionsICasted(String email, long voteId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NoSuchElementException::new);

        Vote vote = voteService.readVote(voteId);

        return vote.castedOptions(member.getEmail()).stream()
                .map(VoteOption::getId)
                .collect(Collectors.toList());
    }
}
