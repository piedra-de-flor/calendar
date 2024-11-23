package com.example.calendar.domain.vo.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationRedirectUrl {
    INVITATION_FRIEND("-"),
    INVITATION_TEAM("-"),
    VOTE_CREATED("-"),
    VOTE_END("-");

    private final String url;
}
