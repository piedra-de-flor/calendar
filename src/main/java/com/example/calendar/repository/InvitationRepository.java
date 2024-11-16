package com.example.calendar.repository;

import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.vo.invitation.InvitationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByStateEquals(InvitationState state);
}
