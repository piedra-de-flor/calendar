package com.example.calendar.service.invitation;

import com.example.calendar.domain.entity.group.Group;
import com.example.calendar.domain.entity.group.Grouping;
import com.example.calendar.domain.entity.invitation.FriendInvitation;
import com.example.calendar.domain.entity.invitation.GroupInvitation;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationState;
import com.example.calendar.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;

    @Transactional
    public Invitation createFriendInvitation(Member sender, Member receiver) {
        Invitation friendInvitation = FriendInvitation.builder()
                .sender(sender)
                .receiver(receiver)
                .build();

        invitationRepository.save(friendInvitation);
        return friendInvitation;
    }

    @Transactional
    public Invitation createGroupInvitation(Member sender, Member receiver, Group group, Grouping grouping) {
        Invitation groupInvitation = GroupInvitation.builder()
                .sender(sender)
                .receiver(receiver)
                .group(group)
                .grouping(grouping)
                .build();

        invitationRepository.save(groupInvitation);
        return groupInvitation;
    }

    public List<Invitation> realAllSendInvitations(Member member) {
        return member.getInvitations().stream()
                .filter(invitation -> isSent(invitation, member))
                .collect(Collectors.toList());
    }

    public List<Invitation> realAllReceiveInvitations(Member member) {
        return member.getInvitations().stream()
                .filter(invitation -> isReceived(invitation, member))
                .collect(Collectors.toList());
    }

    public Invitation readInvitation(long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(NoSuchElementException::new);
    }

    private boolean isReceived(Invitation invitation, Member member) {
        Member receiver = invitation.getReceiver();
        return receiver.getEmail().equals(member.getEmail());
    }

    private boolean isSent(Invitation invitation, Member member) {
        Member sender = invitation.getSender();
        return sender.getEmail().equals(member.getEmail());
    }

    @Transactional
    public boolean cancelInvitation(Member member, Invitation invitation) {
        if (isSent(invitation, member)) {
            invitationRepository.delete(invitation);
        }

        return true;
    }

    @Scheduled(cron = "0 59 23 * * *")
    private void deleteGroup() {
        List<Invitation> targetInvitations = invitationRepository.findAllByStateEquals(InvitationState.DENIED);
        invitationRepository.deleteAll(targetInvitations);
        log.info("Deleted {} denied invitations", targetInvitations.size());
    }
}
