package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.notification.NotificationContents;
import org.springframework.stereotype.Service;

@Service
public class NotificationContentService {
    public String makeFriendInvitationContent(Member sender) {
        return sender.getName() + NotificationContents.SENDER + NotificationContents.FRIEND_INVITATION;
    }

    public String inviteTeam(Member sender, Team team) {
        return sender.getName() + NotificationContents.SENDER + team.getName() + NotificationContents.TEAM_INVITATION;
    }
}
