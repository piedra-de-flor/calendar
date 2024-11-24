package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.invitation.FriendInvitationDto;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.service.invitation.InvitationFacadeService;
import com.example.calendar.service.notification.NotificationFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class FriendFacadeService {
    private final MemberRepository memberRepository;
    private final InvitationFacadeService invitationFacadeService;

    @Transactional
    public boolean createFriend(String memberEmail, long friendId) {
        Member sender = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Member receiver = memberRepository.findById(friendId)
                .orElseThrow(NoSuchElementException::new);

        invitationFacadeService.sendFriendInvitation(sender.getEmail(), new FriendInvitationDto(receiver.getEmail()));
        return true;
    }
}
