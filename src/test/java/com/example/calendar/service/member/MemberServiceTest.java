package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.member.MemberDto;
import com.example.calendar.dto.member.MemberUpdateDto;
import com.example.calendar.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원_단일_조회_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getName()).thenReturn("Test User");

        // When
        MemberDto result = memberService.readMemberInfo(memberEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(memberEmail);
        assertThat(result.name()).isEqualTo("Test User");
        verify(memberRepository, times(1)).findByEmail(memberEmail);
    }

    @Test
    void 회원_단일_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.readMemberInfo(memberEmail))
                .isInstanceOf(NoSuchElementException.class);

        verify(memberRepository, times(1)).findByEmail(memberEmail);
    }

    @Test
    void 회원_수정_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        MemberUpdateDto updateDto = new MemberUpdateDto("New Name", "oldPassword", "newPassword");
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getPassword()).thenReturn("oldPassword");

        // When
        MemberUpdateDto result = memberService.update(updateDto, memberEmail);

        // Then
        assertThat(result).isEqualTo(updateDto);
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(member, times(1)).update("New Name", "oldPassword");
    }

    @Test
    void 회원_수정_비밀번호_불일치_테스트() {
        // Given
        String memberEmail = "user@example.com";
        MemberUpdateDto updateDto = new MemberUpdateDto("New Name", "incorrectPassword", "newPassword");
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getPassword()).thenReturn("oldPassword");

        // When
        MemberUpdateDto result = memberService.update(updateDto, memberEmail);

        // Then
        assertThat(result).isEqualTo(updateDto);
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(member, never()).update(anyString(), anyString());
    }

    @Test
    void 회원_삭제_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        String password = "password123";
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getPassword()).thenReturn(password);

        // When
        boolean result = memberService.delete(memberEmail, password);

        // Then
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    void 회원_삭제_비밀번호_불일치_테스트() {
        // Given
        String memberEmail = "user@example.com";
        String password = "incorrectPassword";
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getPassword()).thenReturn("password123");

        // When
        boolean result = memberService.delete(memberEmail, password);

        // Then
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, never()).delete(member);
    }

    @Test
    void 회원_삭제_회원_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";
        String password = "password123";

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.delete(memberEmail, password))
                .isInstanceOf(NoSuchElementException.class);

        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, never()).delete(any(Member.class));
    }
}
