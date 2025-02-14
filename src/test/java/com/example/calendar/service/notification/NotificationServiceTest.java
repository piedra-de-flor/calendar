package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.notification.Notification;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.notification.NotificationDto;
import com.example.calendar.repository.EmitterRepository;
import com.example.calendar.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    @InjectMocks
    private NotificationService notificationService;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private EmitterRepository emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 알림_구독_성공_테스트() {
        // Given
        String email = "user@example.com";
        String lastEventId = "12345";
        SseEmitter mockEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        when(emitterRepository.save(anyString(), any(SseEmitter.class))).thenReturn(mockEmitter);
        when(emitterRepository.findAllEventCacheStartsWithEmail(email)).thenReturn(Collections.emptyMap());

        // When
        SseEmitter result = notificationService.subscribe(email, lastEventId);

        // Then
        assertThat(result).isNotNull();
        verify(emitterRepository, times(1)).save(anyString(), any(SseEmitter.class));
        verify(emitterRepository, times(1)).findAllEventCacheStartsWithEmail(email);
    }

    @Test
    void 알림_전송_성공_테스트() throws IOException {
        // Given
        Member receiver = mock(Member.class);
        String receiverEmail = "user@example.com";
        NotificationType notificationType = NotificationType.INVITATION;
        String content = "Test Notification";
        String redirectUrl = "/redirect";

        when(receiver.getEmail()).thenReturn(receiverEmail);
        Map<String, SseEmitter> emitters = new HashMap<>();
        SseEmitter mockEmitter = mock(SseEmitter.class);
        emitters.put("emitter1", mockEmitter);
        when(emitterRepository.findAllEmitterStartsWithEmail(receiverEmail)).thenReturn(emitters);

        // When
        notificationService.send(receiver, notificationType, content, redirectUrl);

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emitterRepository, times(1)).findAllEmitterStartsWithEmail(receiverEmail);
        verify(mockEmitter, times(1)).send(any(SseEmitter.event().getClass()));
        verify(emitterRepository, times(1)).saveEventCache(anyString(), any(Notification.class));
    }

    @Test
    void 알림_전체_조회_성공_테스트() {
        // Given
        String email = "user@example.com";
        int page = 0;
        int size = 10;

        Notification notification1 = mock(Notification.class);
        Notification notification2 = mock(Notification.class);

        when(notification1.getId()).thenReturn(1L);
        when(notification2.getId()).thenReturn(2L);

        Page<Notification> mockPage = new PageImpl<>(List.of(notification1, notification2));
        when(notificationRepository.findByReceiverEmail(eq(email), any(Pageable.class))).thenReturn(mockPage);

        // When
        List<NotificationDto> result = notificationService.getNotifications(email, page, size);

        // Then
        assertThat(result).hasSize(2);
        verify(notificationRepository, times(1)).findByReceiverEmail(eq(email), any(Pageable.class));
    }

    @Test
    void 알림_읽기_처리_성공_테스트() {
        // Given
        String email = "user@example.com";
        long notificationId = 1L;
        Notification notification = mock(Notification.class);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notification.getReceiver()).thenReturn(mock(Member.class));
        when(notification.getReceiver().getEmail()).thenReturn(email);

        // When
        notificationService.markAsRead(email, notificationId);

        // Then
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notification, times(1)).read();
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void 알림_읽기_처리_권한_실패_테스트() {
        // Given
        String email = "user@example.com";
        long notificationId = 1L;
        Notification notification = mock(Notification.class);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notification.getReceiver()).thenReturn(mock(Member.class));
        when(notification.getReceiver().getEmail()).thenReturn("another@example.com");

        // When & Then
        assertThatThrownBy(() -> notificationService.markAsRead(email, notificationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("you don't have auth to read the notification");

        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
