package com.example.calendar.controller.member;

import com.example.calendar.dto.member.JwtToken;
import com.example.calendar.dto.member.SignInDto;
import com.example.calendar.dto.member.SignUpDto;
import com.example.calendar.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class MemberController {
    private final MemberService service;

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
}