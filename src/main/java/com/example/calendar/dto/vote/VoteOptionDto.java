package com.example.calendar.dto.vote;

import com.example.calendar.dto.member.MemberDto;

import java.util.List;

public record VoteOptionDto (
        String optionText,
        List<MemberDto> voters
) {
}
