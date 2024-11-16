package com.example.calendar.controller.member;

import com.example.calendar.dto.member.JwtToken;
import com.example.calendar.dto.member.MemberUpdateDto;
import com.example.calendar.dto.member.SignInDto;
import com.example.calendar.dto.member.SignUpDto;
import com.example.calendar.service.member.MemberFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberFacadeService service;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto userJoinRequestDto) {
        String response = service.signUp(userJoinRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> signIn(@RequestBody SignInDto signInDto) {
        JwtToken token = service.signIn(signInDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/sign-up/valid/name")
    public ResponseEntity<Boolean> canName(@RequestBody String name) {
        boolean response = service.canName(name);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/member")
    public ResponseEntity<MemberUpdateDto> update(@RequestBody MemberUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        MemberUpdateDto response = service.update(updateDto, memberEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/member")
    public ResponseEntity<Boolean> delete(@RequestBody String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = service.delete(memberEmail, password);
        return ResponseEntity.ok(response);
    }
}