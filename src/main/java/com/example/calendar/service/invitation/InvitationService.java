package com.example.calendar.service.invitation;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.invitation.FriendInvitation;
import com.example.calendar.domain.entity.invitation.TeamInvitation;
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

@RequiredArgsConstructor
@Slf4j
@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;

    @Transactional
    public Invitation createFriendInvitation(Member sender, Member receiver) {
        Invitation friendInvitation = new FriendInvitation(sender, receiver);

        invitationRepository.save(friendInvitation);
        return friendInvitation;
    }

    @Transactional
    public Invitation createTeamInvitation(Member sender, Member receiver, Team team) {
        Invitation teamInvitation = new TeamInvitation(receiver, sender, team);

        invitationRepository.save(teamInvitation);
        return teamInvitation;
    }

    public List<Invitation> realAllSendInvitations(Member member) {
        return member.getSentInvitations();
    }

    public List<Invitation> realAllReceiveInvitations(Member member) {
        return member.getReceivedInvitations();
    }

    public Invitation readInvitation(long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public boolean cancelInvitation(Member member, Invitation invitation) {
        if (invitation.isSentBy(member)) {
            deleteInvitation(invitation.getId());
        }

        return true;
    }

    public void deleteInvitation(long invitationID) {
        invitationRepository.deleteById(invitationID);
    }
}
