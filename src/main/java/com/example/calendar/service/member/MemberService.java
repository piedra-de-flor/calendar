package com.example.calendar.service.member;

import com.example.calendar.dto.member.JwtToken;
import com.example.calendar.dto.member.SignInDto;
import com.example.calendar.dto.member.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final SignInService signInService;
    private final SignUpService signUpService;

    public String signUp(SignUpDto signUpDto) {
        return signUpService.signUp(signUpDto.email(), signUpDto.password(), signUpDto.name());
    }

    public JwtToken signIn(SignInDto signInDto) {
        return signInService.signIn(signInDto.email(), signInDto.password());
    }
}
