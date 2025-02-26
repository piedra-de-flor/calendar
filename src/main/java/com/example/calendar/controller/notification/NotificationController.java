package com.example.calendar.controller.notification;

import com.example.calendar.dto.notification.NotificationDto;
import com.example.calendar.service.notification.NotificationFacadeService;
import com.example.calendar.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NotificationController {
    private final NotificationFacadeService notificationService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping(value = "/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @RequestParam String token,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Authentication authentication = tokenProvider.getAuthentication(token);
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
