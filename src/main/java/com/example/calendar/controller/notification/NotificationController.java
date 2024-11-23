package com.example.calendar.controller.notification;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.notification.NotificationDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
public class NotificationController {
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        return notificationService.subscribe(memberEmail, lastEventId);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<NotificationDto> notifications = notificationService.getNotifications(memberEmail, page, size);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/notification/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        notificationService.markAsRead(memberEmail, notificationId);
        return ResponseEntity.noContent().build();
    }
}
