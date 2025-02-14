package com.example.calendar.controller.member;

import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.service.member.FriendFacadeService;
import com.example.calendar.service.member.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FriendController {
    private final FriendFacadeService friendFacadeService;
    private final FriendService friendService;

    @PostMapping("/friend")
    public ResponseEntity<Boolean> createFriend(@RequestParam long friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = friendFacadeService.createFriend(memberEmail, friendId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-friends")
    public ResponseEntity<List<FriendDto>> readAllFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<FriendDto> response = friendService.realAllFriends(memberEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/friend")
    public ResponseEntity<Boolean> deleteFriend(@RequestParam long friendId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = friendService.deleteFriend(memberEmail, friendId);
        return ResponseEntity.ok(response);
    }
}
