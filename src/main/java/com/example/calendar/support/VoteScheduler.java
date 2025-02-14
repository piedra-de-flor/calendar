package com.example.calendar.support;

import com.example.calendar.service.vote.VoteFacadeService;
import com.example.calendar.service.vote.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VoteScheduler {
    private final VoteService voteService;

    @Scheduled(cron = "0 0 * * * *")
    public void manageVotes() {
        voteService.closeExpiredVotes();
        voteService.deleteExpiredVotes();
    }
}

