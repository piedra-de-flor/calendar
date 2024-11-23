package com.example.calendar.dto.vote;

import java.util.List;

public record VoteCreateDto (
        long teamId,
        String voteTitle,
        String voteDescription,
        boolean isMultipleVote,
        List<String> voteOptions
) {
}
