package com.example.calendar.dto.vote;

import com.example.calendar.dto.exception.RequestValidationException;

import java.util.List;

public record VoteCreateDto (
        long teamId,
        String voteTitle,
        String voteDescription,
        boolean isMultipleVote,
        List<String> voteOptions
) {
    public VoteCreateDto {
        requireNonNull(voteTitle, "Title must not be null");

        if (voteTitle.length() > 10) {
            throw new RequestValidationException("Vote title must be between 1 and 10 characters");
        }

        if (voteTitle.startsWith(" ") || voteTitle.endsWith(" ")) {
            throw new RequestValidationException("Vote title should not start or end with blank");
        }

        if (voteDescription.length() > 30) {
            throw new RequestValidationException("Vote description must be between 1 and 10 characters");
        }
    }

    private static <T> void requireNonNull(T obj, String message) {
        if (obj == null)
            throw new RequestValidationException(message);
    }
}
