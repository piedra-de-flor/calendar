package com.example.calendar.domain.vo.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationContents {
    SENDER("님이"),
    TEAM_INVITATION(" 팀에 초대를 했습니다."),
    FRIEND_INVITATION(" 친구를 요청하셨습니다."),
    ACCEPT_INVITATION("요청을 수락하셨습니다.");

    private final String content;
}
