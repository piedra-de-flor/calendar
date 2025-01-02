package com.example.calendar.controller.member;

import com.example.calendar.dto.member.*;
import com.example.calendar.service.member.MemberFacadeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    public ResponseEntity<Void> signIn(@RequestBody SignInDto signInDto, HttpServletResponse response) {
        JwtToken token = service.signIn(signInDto);
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token.getAccessToken())
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member")
    public ResponseEntity<MemberDto> readMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        MemberDto response = service.read(memberEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sign-up/valid")
    public ResponseEntity<Boolean> EmailDuplicatedCheck(@RequestParam String email) {
        log.info("email : {}", email);
        boolean response = service.isDuplicated(email);
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