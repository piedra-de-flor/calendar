package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationFacadeService {
    private final NotificationService notificationService;
    private final NotificationContentService contentService;

    public SseEmitter subscribe(String email, String lastEventId) {
        return notificationService.subscribe(email, lastEventId);
    }

    public void send(Member receiver, NotificationType notificationType, String content, String redirectUrl) {
        notificationService.send(receiver, notificationType, content, redirectUrl);
    }

    public List<NotificationDto> getNotifications(String email, int page, int size) {
        return notificationService.getNotifications(email, page, size);
    }

    public void markAsRead(String email, long notificationId) {
        notificationService.markAsRead(email, notificationId);
    }

    public String inviteFriendMessage(Member sender) {
        return contentService.makeFriendInvitationContent(sender);
    }

    public String inviteTeamMessage(Member sender, Team team) {
        return contentService.inviteTeam(sender, team);
    }

    public String voteCreateMessage(Team team) {
        return contentService.createVote(team);
    }

    public String voteCompleteMessage(Vote vote) {
        return contentService.completeVote(vote);
    }

    public String acceptInvitation(Member receiver) {
        return contentService.acceptInvitation(receiver);
    }
}
