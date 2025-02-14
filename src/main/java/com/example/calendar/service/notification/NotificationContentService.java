package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.vo.notification.NotificationContents;
import org.springframework.stereotype.Service;

@Service
public class NotificationContentService {
    public String makeFriendInvitationContent(Member sender) {
        return sender.getName() + NotificationContents.SENDER.getContent() + NotificationContents.FRIEND_INVITATION.getContent();
    }

    public String inviteTeam(Member sender, Team team) {
        return sender.getName() + NotificationContents.SENDER.getContent() + " " + team.getName() + NotificationContents.TEAM_INVITATION.getContent();
    }

    public String acceptInvitation(Member receiver) {
        return receiver.getName() + NotificationContents.SENDER.getContent() + NotificationContents.ACCEPT_INVITATION.getContent();
    }

    public String createVote(Team team) {
        return team.getName() + NotificationContents.SENDER_TEAM.getContent() + NotificationContents.CREATE_VOTE.getContent();
    }

    public String completeVote(Vote vote) {
        return vote.getTeam().getName() + NotificationContents.SENDER_TEAM.getContent() + " " + vote.getTitle() + NotificationContents.COMPLETE_VOTE.getContent();
    }
}
