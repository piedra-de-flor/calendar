package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.notification.Notification;
import com.example.calendar.domain.vo.notification.NotificationContents;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public String inviteFriend(Member sender) {
        return contentService.makeFriendInvitationContent(sender);
    }

    public String inviteTeam(Member sender, Team team) {
        return contentService.inviteTeam(sender, team);
    }

    public String acceptInvitation(Member receiver) {
        return contentService.acceptInvitation(receiver);
    }
}
