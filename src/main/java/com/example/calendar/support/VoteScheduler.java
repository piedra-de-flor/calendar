package com.example.calendar.support;

import com.example.calendar.service.vote.VoteService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VoteScheduler {

    private final VoteService voteService;

    public VoteScheduler(VoteService voteService) {
        this.voteService = voteService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void manageVotes() {
        voteService.closeExpiredVotes();
        voteService.deleteExpiredVotes();
    }
}

