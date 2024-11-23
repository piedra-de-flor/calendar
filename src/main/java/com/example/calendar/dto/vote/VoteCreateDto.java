package com.example.calendar.dto.vote;

import java.util.List;

public record VoteCreateDto (
        long teamId,
        String VoteTitle,
        String VoteDescription,
        boolean isMultipleVote,
        List<String> voteOptions
) {
}
