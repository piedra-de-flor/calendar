package com.example.calendar.dto.vote;

import java.util.Map;

public record VoteDto (
        long voteId,
        String voteTitle,
        String description,
        Map<String, Map<Long, Integer>> options
) {
}
