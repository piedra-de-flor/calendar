package com.example.calendar.service.invitation;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.invitation.FriendInvitation;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.invitation.TeamInvitation;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationState;
import com.example.calendar.domain.vo.invitation.InvitationType;
import com.example.calendar.domain.vo.notification.NotificationRedirectUrl;
import com.example.calendar.domain.vo.notification.NotificationType;
import com.example.calendar.dto.invitation.FriendInvitationDto;
import com.example.calendar.dto.invitation.InvitationDto;
import com.example.calendar.dto.invitation.TeamInvitationDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.repository.TeamingRepository;
import com.example.calendar.service.notification.NotificationFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InvitationFacadeService {
    private final InvitationService invitationService;
    private final NotificationFacadeService notificationService;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamingRepository teamingRepository;

    @Transactional
    public boolean sendFriendInvitation(String memberEmail, FriendInvitationDto invitationDto) {
        Member sender = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Member receiver = memberRepository.findByEmail(invitationDto.receiverEmail())
                .orElseThrow(NoSuchElementException::new);

        if (sender.isFriend(receiver) || alreadySendFriendInvitation(sender, receiver)) {
            throw new IllegalArgumentException("send Friend invitation error");
        }

        Invitation friendInvitation = invitationService.createFriendInvitation(sender, receiver);
        receiver.addInvitation(friendInvitation);

        notificationService.send(receiver, NotificationType.INVITATION, notificationService.inviteFriendMessage(sender), NotificationRedirectUrl.INVITATION_FRIEND.getUrl());
        return true;
    }

    @Transactional
    public boolean sendTeamInvitation(String memberEmail, TeamInvitationDto invitationDto) {
        Member sender = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Member receiver = memberRepository.findByEmail(invitationDto.receiverEmail())
                .orElseThrow(NoSuchElementException::new);

        Team team = teamRepository.findById(invitationDto.teamId())
                .orElseThrow(NoSuchElementException::new);

        if (!sender.inTheTeam(invitationDto.teamId()) || receiver.inTheTeam(invitationDto.teamId()) || alreadySendTeamInvitation(sender, receiver, team.getId())) {
            throw new IllegalArgumentException("send Team invitation error");
        }

        Invitation groupInvitation = invitationService.createTeamInvitation(sender, receiver, team);
        receiver.addInvitation(groupInvitation);

        notificationService.send(receiver, NotificationType.INVITATION, notificationService.inviteTeamMessage(sender, team), NotificationRedirectUrl.INVITATION_TEAM.getUrl());
        return true;
    }

    @Transactional
    public boolean acceptInvitation(long invitationId) {
        Invitation invitation = invitationService.readInvitation(invitationId);

        if (invitation instanceof TeamInvitation) {
            Teaming teaming = Teaming.builder()
                    .team(((TeamInvitation) invitation).getTeam())
                    .member(invitation.getReceiver())
                    .build();

            invitation.getReceiver().addTeam(teaming);
            ((TeamInvitation) invitation).getTeam().addTeaming(teaming);
            teamingRepository.save(teaming);
        } else {
            invitation.getReceiver().addFriends(invitation.getSender().getId());
            invitation.getSender().addFriends(invitation.getReceiver().getId());
        }

        invitationService.deleteInvitation(invitationId);

        notificationService.send(invitation.getSender(), NotificationType.INVITATION, notificationService.acceptInvitation(invitation.getReceiver()), NotificationRedirectUrl.INVITATION_TEAM.getUrl());
        return true;
    }

    @Transactional
    public boolean deniedInvitation(long invitationId) {
        Invitation invitation = invitationService.readInvitation(invitationId);

        invitation.denied();
        return true;
    }

    public List<InvitationDto> realAllReceiveFriendInvitations(String memberEmail) {
        List<InvitationDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        List<Invitation> invitations = invitationService.realAllReceiveInvitations(member).stream()
                .filter(invitation -> invitation instanceof FriendInvitation)
                .filter(invitation -> invitation.getState().equals(InvitationState.NOT_DECIDE))
                .collect(Collectors.toList());

        return classifyInvitations(response, invitations);
    }

    public List<InvitationDto> realAllSendFriendInvitations(String memberEmail) {
        List<InvitationDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        List<Invitation> invitations = invitationService.realAllSendInvitations(member).stream()
                .filter(invitation -> invitation instanceof FriendInvitation)
                .collect(Collectors.toList());

        return classifyInvitations(response, invitations);
    }

    public List<InvitationDto> realAllReceiveTeamInvitations(String memberEmail) {
        List<InvitationDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        List<Invitation> invitations = invitationService.realAllReceiveInvitations(member).stream()
                .filter(invitation -> invitation instanceof TeamInvitation)
                .filter(invitation -> invitation.getState().equals(InvitationState.NOT_DECIDE))
                .collect(Collectors.toList());

        return classifyInvitations(response, invitations);
    }

    public List<InvitationDto> realAllSendTeamInvitations(String memberEmail) {
        List<InvitationDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        List<Invitation> invitations = invitationService.realAllSendInvitations(member).stream()
                .filter(invitation -> invitation instanceof TeamInvitation)
                .collect(Collectors.toList());

        return classifyInvitations(response, invitations);
    }

    private List<InvitationDto> classifyInvitations(List<InvitationDto> response, List<Invitation> invitations) {
        for (Invitation invitation : invitations) {
            InvitationDto invitationDto;

            if (invitation instanceof TeamInvitation) {
                invitationDto = new InvitationDto(
                        invitation.getId(),
                        InvitationType.TEAM.getType(),
                        invitation.getSender().getEmail(),
                        invitation.getSender().getName(),
                        invitation.getReceiver().getEmail(),
                        invitation.getReceiver().getName(),
                        ((TeamInvitation) invitation).getTeam().getName());
            } else {
                invitationDto = new InvitationDto(
                        invitation.getId(),
                        InvitationType.FRIEND.getType(),
                        invitation.getSender().getEmail(),
                        invitation.getSender().getName(),
                        invitation.getReceiver().getEmail(),
                        invitation.getReceiver().getName(),
                        Optional.empty());
            }

            response.add(invitationDto);
        }

        return response;
    }

    public boolean cancelInvitation(String memberEmail, long invitationId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Invitation invitation = invitationService.readInvitation(invitationId);
        return invitationService.cancelInvitation(member, invitation);
    }

    private boolean alreadySendFriendInvitation(Member sender, Member receiver) {
        boolean senderSent = sender.getSentInvitations().stream()
                .filter(invitation -> invitation instanceof FriendInvitation)
                .filter(invitation -> invitation.getState().equals(InvitationState.NOT_DECIDE))
                .anyMatch(invitation -> invitation.getReceiver().getId() == receiver.getId());

        boolean senderReceived = sender.getReceivedInvitations().stream()
                .filter(invitation -> invitation instanceof FriendInvitation)
                .filter(invitation -> invitation.getState().equals(InvitationState.NOT_DECIDE))
                .anyMatch(invitation -> invitation.getSender().getId() == receiver.getId());

        return senderSent || senderReceived;
    }

    private boolean alreadySendTeamInvitation(Member sender, Member receiver, long teamId) {
        boolean senderSent = sender.getSentInvitations().stream()
                .filter(invitation -> invitation instanceof TeamInvitation)
                .filter(invitation -> ((TeamInvitation) invitation).getTeam().getId() == teamId)
                .filter(invitation -> invitation.getState().equals(InvitationState.NOT_DECIDE))
                .anyMatch(invitation -> invitation.getReceiver().getId() == receiver.getId());

        boolean senderReceived = sender.getReceivedInvitations().stream()
                .filter(invitation -> invitation instanceof TeamInvitation)
                .filter(invitation -> ((TeamInvitation) invitation).getTeam().getId() == teamId)
                .filter(invitation -> invitation.getState().equals(InvitationState.NOT_DECIDE))
                .anyMatch(invitation -> invitation.getSender().getId() == receiver.getId());

        return senderSent || senderReceived;
    }
}
