package com.example.calendar.service.member;

import com.example.calendar.dto.member.JwtToken;
import com.example.calendar.dto.member.MemberUpdateDto;
import com.example.calendar.dto.member.SignInDto;
import com.example.calendar.dto.member.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberFacadeService {
    private final MemberService memberService;
    private final SignInService signInService;
    private final SignUpService signUpService;

    public String signUp(SignUpDto signUpDto) {
        return signUpService.signUp(signUpDto.email(), signUpDto.password(), signUpDto.name());
    }

    public JwtToken signIn(SignInDto signInDto) {
        return signInService.signIn(signInDto.email(), signInDto.password());
    }

    public boolean canName(String name) {
        return signUpService.canName(name);
    }

    public MemberUpdateDto update(MemberUpdateDto updateDto, String email) {
        return memberService.update(updateDto, email);
    }

    public boolean delete(String email, String password) {
        return memberService.delete(email, password);
    }
}
