package com.example.calendar.dto.vote;

import com.example.calendar.domain.vo.vote.VoteStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record VoteDto (
        long voteId,
        String voteTitle,
        String description,
        VoteStatus voteStatus,
        boolean multiple,
        LocalDateTime createdAt,
        LocalDateTime closeAt,
        Map<String, Map<Long, Integer>> options
) {
}
