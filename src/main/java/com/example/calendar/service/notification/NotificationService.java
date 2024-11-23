package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.notification.Notification;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.notification.NotificationDto;
import com.example.calendar.repository.EmitterRepository;
import com.example.calendar.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Transactional
    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = email + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteEmitterById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteEmitterById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteEmitterById(emitterId));

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartsWithEmail(email);
            events.entrySet().stream()
                    .filter(entry -> entry.getKey().compareTo(lastEventId) > 0)
                    .forEach(entry -> {
                        try {
                            emitter.send(SseEmitter.event().name("resend").data(entry.getValue()));
                        } catch (IOException e) {
                            log.error("Error resending event: {}", e.getMessage());
                        }
                    });
        }

        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected successfully"));
        } catch (IOException e) {
            log.error("Error sending connect event: {}", e.getMessage());
        }

        return emitter;
    }

    @Transactional
    public void send(Member receiver, NotificationType notificationType, String content, String redirectUrl) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(content)
                .redirectUrl(redirectUrl)
                .build();

        notificationRepository.save(notification);

        String receiverEmail = receiver.getEmail();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartsWithEmail(receiverEmail);

        emitters.forEach((key, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(convertToDto(notification)));
            } catch (IOException e) {
                emitterRepository.deleteEmitterById(key);
                log.error("Error sending notification: {}", e.getMessage());
            }
        });

        String eventCacheId = receiverEmail + "_" + System.currentTimeMillis();
        emitterRepository.saveEventCache(eventCacheId, notification);
    }

    public List<NotificationDto> getNotifications(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Notification> notifications = notificationRepository.findByReceiverEmail(email, pageable);
        List<NotificationDto> response = new ArrayList<>();

        for (NotificationDto dto : notifications.map(this::convertToDto)) {
            response.add(dto);
        }

        return response;
    }

    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .redirectUrl(notification.getRedirectUrl())
                .notificationType(notification.getNotificationType())
                .isRead(notification.isRead())
                .build();
    }

    @Transactional
    public void markAsRead(String email, long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (notification.getReceiver().getEmail().equals(email)) {
            notification.read();
            notificationRepository.save(notification);
        }

        throw new IllegalArgumentException("you don't have auth to read the notification");
    }
}