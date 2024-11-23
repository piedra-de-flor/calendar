package com.example.calendar.repository;

import com.example.calendar.domain.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverEmail(String email, Pageable pageable);
}
