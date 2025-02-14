package com.example.calendar.service.member;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.dto.member.FriendDto;
import com.example.calendar.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FriendServiceTest {
    @InjectMocks
    private FriendService friendService;
    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 친구_전체_조회_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        Member member = mock(Member.class);
        Member friend1 = mock(Member.class);
        Member friend2 = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getFriends()).thenReturn(List.of(1L, 2L));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(friend1));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(friend2));
        when(friend1.getId()).thenReturn(1L);
        when(friend1.getEmail()).thenReturn("friend1@example.com");
        when(friend1.getName()).thenReturn("Friend One");
        when(friend2.getId()).thenReturn(2L);
        when(friend2.getEmail()).thenReturn("friend2@example.com");
        when(friend2.getName()).thenReturn("Friend Two");

        // When
        List<FriendDto> result = friendService.realAllFriends(memberEmail);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(FriendDto::email).containsExactlyInAnyOrder("friend1@example.com", "friend2@example.com");
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, times(2)).findById(anyLong());
    }

    @Test
    void 친구_전체_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> friendService.realAllFriends(memberEmail))
                .isInstanceOf(NoSuchElementException.class);

        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, never()).findById(anyLong());
    }

    @Test
    void 친구_삭제_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long friendId = 1L;

        Member member = mock(Member.class);
        Member friend = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(memberRepository.findById(friendId)).thenReturn(Optional.of(friend));

        // When
        boolean result = friendService.deleteFriend(memberEmail, friendId);

        // Then
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, times(1)).findById(friendId);
        verify(member, times(1)).deleteFriends(friendId);
        verify(friend, times(1)).deleteFriends(member.getId());
    }

    @Test
    void 친구_삭제_회원_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long friendId = 1L;

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> friendService.deleteFriend(memberEmail, friendId))
                .isInstanceOf(NoSuchElementException.class);

        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, never()).findById(anyLong());
    }

    @Test
    void 친구_삭제_친구_조회_실패_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long friendId = 1L;

        Member member = mock(Member.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(memberRepository.findById(friendId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> friendService.deleteFriend(memberEmail, friendId))
                .isInstanceOf(NoSuchElementException.class);

        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(memberRepository, times(1)).findById(friendId);
    }
}
