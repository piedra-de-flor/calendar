package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
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

    public String acceptInvitation(Member receiver) {
        return receiver.getName() + NotificationContents.SENDER + NotificationContents.ACCEPT_INVITATION;
    }

    public String createVote(Team team) {
        return team.getName() + NotificationContents.SENDER_TEAM + NotificationContents.CREATE_VOTE;
    }

    public String completeVote(Vote vote) {
        return vote.getTeam().getName() + NotificationContents.SENDER_TEAM + vote.getTitle() + NotificationContents.COMPLETE_VOTE;
    }
}
