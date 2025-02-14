package com.example.calendar.dto.notification;

import com.example.calendar.domain.vo.notification.NotificationType;
import lombok.Builder;

@Builder
public record NotificationDto (
    long id,
    String content,
    String redirectUrl,
    NotificationType notificationType,
    boolean isRead
) {
}
