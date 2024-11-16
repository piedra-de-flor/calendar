package com.example.calendar.controller.invitation;

import com.example.calendar.dto.invitation.FriendInvitationDto;
import com.example.calendar.dto.invitation.GroupInvitationDto;
import com.example.calendar.dto.invitation.InvitationDto;
import com.example.calendar.service.invitation.InvitationFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class InvitationController {
    private final InvitationFacadeService invitationFacadeService;

    @PostMapping("/invitation/friend")
    public ResponseEntity<Boolean> createFriendInvitation(@RequestBody FriendInvitationDto invitationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = invitationFacadeService.sendFriendInvitation(memberEmail, invitationDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitation/group")
    public ResponseEntity<Boolean> createGroupInvitation(@RequestBody GroupInvitationDto invitationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = invitationFacadeService.sendGroupInvitation(memberEmail, invitationDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitation/accept")
    public ResponseEntity<Boolean> acceptInvitation(@RequestBody long invitationId) {
        boolean response = invitationFacadeService.acceptInvitation(invitationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitation/denied")
    public ResponseEntity<Boolean> deniedInvitation(@RequestBody long invitationId) {
        boolean response = invitationFacadeService.deniedInvitation(invitationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invitations/receive")
    public ResponseEntity<List<InvitationDto>> readAllReceiveInvitations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<InvitationDto> response = invitationFacadeService.realAllReceiveInvitations(memberEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invitations/send")
    public ResponseEntity<List<InvitationDto>> readAllSendInvitations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<InvitationDto> response = invitationFacadeService.realAllSendInvitations(memberEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/invitation")
    public ResponseEntity<Boolean> cancelInvitation(@RequestBody long invitationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = invitationFacadeService.cancelInvitation(memberEmail, invitationId);
        return ResponseEntity.ok(response);
    }
}
