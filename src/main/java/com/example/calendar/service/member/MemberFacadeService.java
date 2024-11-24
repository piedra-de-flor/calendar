package com.example.calendar.service.member;

import com.example.calendar.dto.member.*;
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

    public MemberDto read(String memberEmail) {
        return memberService.readMemberInfo(memberEmail);
    }

    public boolean isDuplicated(String email) {
        return signUpService.isDuplicated(email);
    }

    public MemberUpdateDto update(MemberUpdateDto updateDto, String email) {
        return memberService.update(updateDto, email);
    }

    public boolean delete(String email, String password) {
        return memberService.delete(email, password);
    }
}
