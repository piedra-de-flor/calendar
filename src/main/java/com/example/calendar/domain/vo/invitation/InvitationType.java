package com.example.calendar.domain.vo.invitation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvitationType {
    TEAM("TEAM"),
    FRIEND("FRIEND");

    private final String type;
}
