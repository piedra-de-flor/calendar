package com.example.calendar.controller.member;

import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.dto.member.GroupAddFriendDto;
import com.example.calendar.dto.member.GroupCreateDto;
import com.example.calendar.service.member.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/group")
    public ResponseEntity<Boolean> createGroup(@RequestBody GroupCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = groupService.createGroup(memberEmail, createDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/group/friend")
    public ResponseEntity<FriendDto> addFriend(@RequestBody GroupAddFriendDto addFriendDto) {
        FriendDto response = groupService.addFriend(addFriendDto);
        return ResponseEntity.ok(response);
    }
}
