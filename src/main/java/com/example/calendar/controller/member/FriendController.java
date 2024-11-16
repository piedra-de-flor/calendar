package com.example.calendar.controller.member;

import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.service.member.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FriendController {
    private final FriendService friendService;

    @GetMapping("/my-friends")
    public ResponseEntity<List<FriendDto>> readAllFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<FriendDto> response = friendService.realAllFriends(memberEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/friend")
    public ResponseEntity<Boolean> deleteFriend(@RequestBody long friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = friendService.deleteFriend(memberEmail, friendId);
        return ResponseEntity.ok(response);
    }
}
