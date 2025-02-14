package com.example.calendar.dto.vote;

import java.util.List;

public record CastVoteOptionsDto(
        List<Long> optionIds
) {
}
