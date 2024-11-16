package com.example.calendar.domain.vo.invitation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvitationState {
    ACCEPT("accept"),
    DENIED("denied"),
    NOT_DECIDE("not_decide");

    private final String state;
}
