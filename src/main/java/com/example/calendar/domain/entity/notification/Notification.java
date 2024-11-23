package com.example.calendar.domain.entity.notification;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.notification.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String redirectUrl;
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiver;

    @Builder
    public Notification(Member receiver, NotificationType notificationType, String content, String redirectUrl) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = content;
        this.redirectUrl = redirectUrl;
    }

    public void read() {
        this.isRead = true;
    }
}
