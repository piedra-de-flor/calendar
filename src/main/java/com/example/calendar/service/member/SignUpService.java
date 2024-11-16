package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SignUpService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String signUp(String email, String password, String name) {
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            throw new IllegalArgumentException("duplicated email");
        }

        Member newMember = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();

        Member savedMember = memberRepository.save(newMember);
        return savedMember.getName();
    }

    public boolean canName(String name) {
        return memberRepository.existsByName(name);
    }
}
