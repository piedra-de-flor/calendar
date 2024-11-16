package com.example.calendar.controller.group;

import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.dto.group.TeamAddFriendDto;
import com.example.calendar.dto.group.TeamCreateDto;
import com.example.calendar.dto.group.TeamDto;
import com.example.calendar.service.group.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/team")
    public ResponseEntity<Boolean> createTeam(@RequestBody TeamCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = teamService.createTeam(memberEmail, createDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/team/friend")
    public ResponseEntity<FriendDto> addFriend(@RequestBody TeamAddFriendDto addFriendDto) {
        FriendDto response = teamService.addFriend(addFriendDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/friends")
    public ResponseEntity<List<FriendDto>> readAllFriendsInTeam(@RequestParam long teamId) {
        List<FriendDto> response = teamService.readAllMemberInTeam(teamId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-teams")
    public ResponseEntity<List<TeamDto>> readAllMyTeams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<TeamDto> response = teamService.readAllMyTeams(memberEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/team")
    public ResponseEntity<Boolean> exitTeam(@RequestParam long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = teamService.exitTeam(memberEmail, teamId);
        return ResponseEntity.ok(response);
    }
}
