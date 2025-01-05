package com.example.calendar.service.member;

import com.example.calendar.dto.member.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MemberFacadeServiceTest {

    @InjectMocks
    private MemberFacadeService memberFacadeService;

    @Mock
    private MemberService memberService;

    @Mock
    private SignInService signInService;

    @Mock
    private SignUpService signUpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원가입_성공_테스트() {
        // Given
        SignUpDto signUpDto = new SignUpDto("user@example.com", "password123", "Test User");
        when(signUpService.signUp(signUpDto.email(), signUpDto.password(), signUpDto.name()))
                .thenReturn("SignUp Success");

        // When
        String result = memberFacadeService.signUp(signUpDto);

        // Then
        assertThat(result).isEqualTo("SignUp Success");
        verify(signUpService, times(1)).signUp(signUpDto.email(), signUpDto.password(), signUpDto.name());
    }

    @Test
    void 로그인_성공_테스트() {
        // Given
        SignInDto signInDto = new SignInDto("user@example.com", "password123");
        JwtToken mockToken = new JwtToken("Bearer", "accessToken", "refreshToken");

        when(signInService.signIn(signInDto.email(), signInDto.password())).thenReturn(mockToken);

        // When
        JwtToken result = memberFacadeService.signIn(signInDto);

        // Then
        assertThat(result).isEqualTo(mockToken);
        verify(signInService, times(1)).signIn(signInDto.email(), signInDto.password());
    }

    @Test
    void 회원_조회_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        MemberDto mockMemberDto = new MemberDto("user@example.com", "Test User");

        when(memberService.readMemberInfo(memberEmail)).thenReturn(mockMemberDto);

        // When
        MemberDto result = memberFacadeService.read(memberEmail);

        // Then
        assertThat(result).isEqualTo(mockMemberDto);
        verify(memberService, times(1)).readMemberInfo(memberEmail);
    }

    @Test
    void 이메일_중복_검증_성공_테스트() {
        // Given
        String email = "user@example.com";

        when(signUpService.isDuplicated(email)).thenReturn(true);

        // When
        boolean result = memberFacadeService.isDuplicated(email);

        // Then
        assertThat(result).isTrue();
        verify(signUpService, times(1)).isDuplicated(email);
    }

    @Test
    void 회원_수정_성공_테스트() {
        // Given
        MemberUpdateDto updateDto = new MemberUpdateDto("New Name", "password123", "newPassword123");
        String email = "user@example.com";

        when(memberService.update(updateDto, email)).thenReturn(updateDto);

        // When
        MemberUpdateDto result = memberFacadeService.update(updateDto, email);

        // Then
        assertThat(result).isEqualTo(updateDto);
        verify(memberService, times(1)).update(updateDto, email);
    }

    @Test
    void 회원_삭제_성공_테스트() {
        // Given
        String email = "user@example.com";
        String password = "password123";

        when(memberService.delete(email, password)).thenReturn(true);

        // When
        boolean result = memberFacadeService.delete(email, password);

        // Then
        assertThat(result).isTrue();
        verify(memberService, times(1)).delete(email, password);
    }
}
