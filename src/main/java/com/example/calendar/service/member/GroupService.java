package com.example.calendar.service.member;

import com.example.calendar.domain.entity.Group;
import com.example.calendar.domain.entity.Grouping;
import com.example.calendar.domain.entity.Member;
import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.dto.member.GroupAddFriendDto;
import com.example.calendar.dto.member.GroupCreateDto;
import com.example.calendar.repository.GroupRepository;
import com.example.calendar.repository.GroupingRepository;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class GroupService {
    private GroupRepository groupRepository;
    private GroupingRepository groupingRepository;
    private MemberRepository memberRepository;

    @Transactional
    public boolean createGroup(String memberEmail, GroupCreateDto createDto) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        if (member.getGroupsNames().contains(createDto.name())) {
            throw new IllegalArgumentException("group name is duplicated");
        }

        Group group = Group.builder()
                .name(createDto.name())
                .build();

        Grouping grouping = Grouping.builder()
                .member(member)
                .group(group)
                .build();

        member.addGroup(grouping);
        groupingRepository.save(grouping);

        for (long friendId : createDto.friends()) {
            Member friend = memberRepository.findById(friendId)
                    .orElseThrow(NoSuchElementException::new);

            Grouping friendGrouping = Grouping.builder()
                    .member(friend)
                    .group(group)
                    .build();

            group.addGrouping(friendGrouping);
            groupingRepository.save(friendGrouping);
        }

        groupRepository.save(group);

        return true;
    }

    @Transactional
    public FriendDto addFriend(GroupAddFriendDto addFriendDto) {
        Member friend = memberRepository.findById(addFriendDto.friendId())
                .orElseThrow(NoSuchElementException::new);

        Group group = groupRepository.findById(addFriendDto.groupId())
                .orElseThrow(NoSuchElementException::new);

        Grouping grouping = Grouping.builder()
                .member(friend)
                .group(group)
                .build();

        groupingRepository.save(grouping);

        return new FriendDto(friend.getId(), friend.getEmail(), friend.getName());
    }
}
