package com.example.calendar.service.invitation;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.invitation.GroupInvitation;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationType;
import com.example.calendar.dto.invitation.FriendInvitationDto;
import com.example.calendar.dto.invitation.TeamInvitationDto;
import com.example.calendar.dto.invitation.InvitationDto;
import com.example.calendar.repository.TeamRepository;
import com.example.calendar.repository.TeamingRepository;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InvitationFacadeService {
    private final InvitationService invitationService;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamingRepository teamingRepository;

    @Transactional
    public boolean sendFriendInvitation(String memberEmail, FriendInvitationDto invitationDto) {
        Member sender = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Member receiver = memberRepository.findByEmail(invitationDto.receiverEmail())
                .orElseThrow(NoSuchElementException::new);

        Invitation friendInvitation = invitationService.createFriendInvitation(sender, receiver);
        receiver.addInvitation(friendInvitation);
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

        Teaming teaming = Teaming.builder()
                .team(team)
                .member(receiver)
                .build();

        Invitation groupInvitation = invitationService.createGroupInvitation(sender, receiver, team, teaming);
        receiver.addInvitation(groupInvitation);
        return true;
    }

    @Transactional
    public boolean acceptInvitation(long invitationId) {
        Invitation invitation = invitationService.readInvitation(invitationId);

        invitation.accept();

        if (invitation.getClass() == GroupInvitation.class) {
            teamingRepository.save(((GroupInvitation) invitation).getTeaming());
        }

        return true;
    }

    @Transactional
    public boolean deniedInvitation(long invitationId) {
        Invitation invitation = invitationService.readInvitation(invitationId);

        invitation.denied();

        return true;
    }

    public List<InvitationDto> realAllReceiveInvitations(String memberEmail) {
        List<InvitationDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        List<Invitation> invitations = invitationService.realAllReceiveInvitations(member);

        return classifyInvitations(response, invitations);
    }

    public List<InvitationDto> realAllSendInvitations(String memberEmail) {
        List<InvitationDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        List<Invitation> invitations = invitationService.realAllSendInvitations(member);

        return classifyInvitations(response, invitations);
    }

    private List<InvitationDto> classifyInvitations(List<InvitationDto> response, List<Invitation> invitations) {
        for (Invitation invitation : invitations) {
            InvitationDto invitationDto;

            if (invitation.getClass() == GroupInvitation.class) {
                invitationDto = new InvitationDto(
                        InvitationType.TEAM.getType(),
                        invitation.getReceiver().getEmail(),
                        invitation.getReceiver().getName(),
                        invitation.getSender().getEmail(),
                        invitation.getSender().getName(),
                        ((GroupInvitation) invitation).getTeam().getName());
            } else {
                invitationDto = new InvitationDto(
                        InvitationType.FRIEND.getType(),
                        invitation.getReceiver().getEmail(),
                        invitation.getReceiver().getName(),
                        invitation.getSender().getEmail(),
                        invitation.getSender().getName(),
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
}
