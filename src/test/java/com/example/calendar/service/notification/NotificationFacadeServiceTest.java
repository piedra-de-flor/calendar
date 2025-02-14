package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.notification.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationFacadeServiceTest {
    @InjectMocks
    private NotificationFacadeService notificationFacadeService;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 알림_구독_성공_테스트() {
        // Given
        String email = "user@example.com";
        String lastEventId = "12345";
        SseEmitter mockEmitter = new SseEmitter();

        when(notificationService.subscribe(email, lastEventId)).thenReturn(mockEmitter);

        // When
        SseEmitter result = notificationFacadeService.subscribe(email, lastEventId);

        // Then
        assertThat(result).isEqualTo(mockEmitter);
        verify(notificationService, times(1)).subscribe(email, lastEventId);
    }

    @Test
    void 알림_전송_성공_테스트() {
        // Given
        Member receiver = mock(Member.class);
        NotificationType notificationType = NotificationType.INVITATION;
        String content = "Test Notification Content";
        String redirectUrl = "/redirect-url";

        // When
        notificationFacadeService.send(receiver, notificationType, content, redirectUrl);

        // Then
        verify(notificationService, times(1)).send(receiver, notificationType, content, redirectUrl);
    }

    @Test
    void 알림_조회_성공_테스트() {
        // Given
        String email = "user@example.com";
        int page = 0;
        int size = 10;
        List<NotificationDto> mockNotifications = List.of(
                new NotificationDto(1L, "Test Content", "/redirect-url", NotificationType.INVITATION, false),
                new NotificationDto(2L, "Another Test Content", "/another-url", NotificationType.INVITATION, false)
        );

        when(notificationService.getNotifications(email, page, size)).thenReturn(mockNotifications);

        // When
        List<NotificationDto> result = notificationFacadeService.getNotifications(email, page, size);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(mockNotifications);
        verify(notificationService, times(1)).getNotifications(email, page, size);
    }

    @Test
    void 알림_읽기_처리_성공_테스트() {
        // Given
        String email = "user@example.com";
        long notificationId = 1L;

        // When
        notificationFacadeService.markAsRead(email, notificationId);

        // Then
        verify(notificationService, times(1)).markAsRead(email, notificationId);
    }
}
