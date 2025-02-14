package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.member.MemberDto;
import com.example.calendar.dto.member.MemberUpdateDto;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDto readMemberInfo(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        return new MemberDto(memberEmail, member.getName());
    }

    @Transactional
    public MemberUpdateDto update(MemberUpdateDto updateDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        if (member.getPassword().equals(updateDto.password())) {
            member.update(updateDto.name(), updateDto.password());
        }

        return updateDto;
    }

    @Transactional
    public boolean delete(String memberEmail, String password) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        if (member.getPassword().equals(password)) {
            memberRepository.delete(member);
        }

        return true;
    }
}
