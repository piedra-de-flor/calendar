package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.member.FriendDto;
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
public class FriendService {
    private final MemberRepository memberRepository;

    public List<FriendDto> realAllFriends(String memberEmail) {
        List<FriendDto> response = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        for (long id : member.getFriends()) {
            Optional<Member> friend = memberRepository.findById(id);

            if (friend.isPresent()) {
                Member existFriend = friend.get();
                response.add(new FriendDto(existFriend.getId(), existFriend.getEmail(), existFriend.getName()));
            }
        }

        return response;
    }

    @Transactional
    public boolean deleteFriend(String memberEmail, long friendId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Member friend = memberRepository.findById(friendId)
                        .orElseThrow(NoSuchElementException::new);

        member.deleteFriends(friendId);
        friend.deleteFriends(member.getId());

        return true;
    }
}
