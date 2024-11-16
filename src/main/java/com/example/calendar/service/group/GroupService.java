package com.example.calendar.service.group;

import com.example.calendar.domain.entity.member.Group;
import com.example.calendar.domain.entity.member.Grouping;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.dto.group.GroupAddFriendDto;
import com.example.calendar.dto.group.GroupCreateDto;
import com.example.calendar.dto.group.GroupDto;
import com.example.calendar.repository.GroupRepository;
import com.example.calendar.repository.GroupingRepository;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
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

    public List<FriendDto> readAllMemberInGroup(long groupId) {
        List<FriendDto> response = new ArrayList<>();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(NoSuchElementException::new);

        for (Grouping grouping : group.getGroupings()) {
            Member groupMember = grouping.getMember();
            response.add(new FriendDto(groupMember.getId(), groupMember.getEmail(), groupMember.getName()));
        }

        return response;
    }

    public List<GroupDto> readAllMyGroups(String memberEmail) {
        List<GroupDto> response = new ArrayList<>();

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        for (Grouping grouping : member.getGroupings()) {
            Group myGroup = grouping.getGroup();
            response.add(new GroupDto(myGroup.getId(), myGroup.getName()));
        }

        return response;
    }

    @Transactional
    public boolean exitGroup(String memberEmail, long groupId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(NoSuchElementException::new);

        Grouping grouping = groupingRepository.findByMemberAndGroup(member, group)
                .orElseThrow(NoSuchElementException::new);

        member.exitGroup(grouping);

        return true;
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void deleteGroup() {
        List<Group> emptyGroups = groupRepository.findAllByGroupingsEmpty();
        groupRepository.deleteAll(emptyGroups);
        log.info("Deleted {} empty groups", emptyGroups.size());
    }
}
