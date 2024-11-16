package com.example.calendar.service.member;

import com.example.calendar.domain.entity.Member;
import com.example.calendar.dto.member.SignUpDto;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SignUpService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public UUID generateUniqueUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (memberRepository.existsById(uuid));
        return uuid;
    }

    @Transactional
    public String signUp(String email, String password, String name) {
        UUID id = generateUniqueUUID();
        Member newMember = Member.builder()
                .id(id)
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();

        Member savedMember = memberRepository.save(newMember);
        return savedMember.getName();
    }
}
