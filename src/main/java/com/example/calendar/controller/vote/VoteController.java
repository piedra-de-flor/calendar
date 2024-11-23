package com.example.calendar.controller.vote;

import com.example.calendar.dto.vote.*;
import com.example.calendar.service.vote.VoteFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class VoteController {
    private final VoteFacadeService voteFacadeService;

    @PostMapping("/vote")
    public ResponseEntity<Boolean> createVote(@RequestBody VoteCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = voteFacadeService.createVote(memberEmail, createDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vote/cast/{voteId}")
    public ResponseEntity<Boolean> vote(@PathVariable long voteId, @RequestBody CastVoteOptionsDto castVoteOptionsDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = voteFacadeService.castVote(memberEmail, voteId, castVoteOptionsDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vote/{voteId}")
    public ResponseEntity<VoteDto> readVote(@PathVariable long voteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        VoteDto response = voteFacadeService.readVote(memberEmail, voteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vote/option/{voteId}/{voteOptionId}")
    public ResponseEntity<VoteOptionDto> readVoteOption(@PathVariable long voteId, @PathVariable long voteOptionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        VoteOptionDto response = voteFacadeService.readVoteOption(memberEmail, voteId, voteOptionId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/vote/status/{voteId}")
    public ResponseEntity<Boolean> completeVote(@PathVariable long voteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = voteFacadeService.completeVote(memberEmail, voteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vote/result/{voteId}")
    public ResponseEntity<VoteResultDto> readResultVote(@PathVariable long voteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        VoteResultDto response = voteFacadeService.getVoteResults(memberEmail, voteId);
        return ResponseEntity.ok(response);
    }
}
