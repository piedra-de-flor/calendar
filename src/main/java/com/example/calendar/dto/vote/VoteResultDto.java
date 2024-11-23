package com.example.calendar.dto.vote;

import lombok.Getter;

import java.util.Map;

@Getter
public class VoteResultDto {
    private final long voteId;
    private final Map<Long, String> results;

    public VoteResultDto(long voteId, Map<Long, String> results) {
        this.voteId = voteId;
        this.results = results;
    }
}