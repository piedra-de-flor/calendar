package com.example.calendar.service.member;

import com.example.calendar.domain.entity.Member;
import com.example.calendar.dto.member.MemberCreateDto;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SignUpService {
    private final MemberRepository memberRepository;

    public UUID generateUniqueUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (memberRepository.existsById(uuid));
        return uuid;
    }

    @Transactional
    public String createMember(MemberCreateDto memberCreateDto) {
        UUID id = generateUniqueUUID();
        Member newMember = Member.builder()
                .id(id)
                .email(memberCreateDto.email())
                .password(memberCreateDto.password())
                .name(memberCreateDto.name())
                .build();

        Member savedMember = memberRepository.save(newMember);
        return savedMember.getNickName();
    }
}
