package com.example.calendar.domain.entity.member;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    void 정보_수정_테스트() {
        member.update("Updated User", "newPassword");

        assertThat(member.getName()).isEqualTo("Updated User");
        assertThat(member.getPassword()).isEqualTo("newPassword");
    }

    @Test
    void 친구_추가_테스트() {
        // When
        member.addFriends(1L);
        member.addFriends(2L);

        // Then
        assertThat(member.getFriends()).contains(1L, 2L);
    }

    @Test
    void 친구_삭제_테스트() {
        // Given
        member.addFriends(1L);
        member.addFriends(2L);

        // When
        member.deleteFriends(1L);

        // Then
        assertThat(member.getFriends()).doesNotContain(1L);
        assertThat(member.getFriends()).contains(2L);
    }

    @Test
    void 팀_추가_테스트() {
        // Given
        Teaming mockTeaming = mock(Teaming.class);

        // When
        member.addTeam(mockTeaming);

        // Then
        assertThat(member.getTeamings()).contains(mockTeaming);
    }

    @Test
    void 팀_제거_테스트() {
        // Given
        Teaming mockTeaming = mock(Teaming.class);
        member.addTeam(mockTeaming);

        // When
        member.exitTeam(mockTeaming);

        // Then
        assertThat(member.getTeamings()).doesNotContain(mockTeaming);
    }

    @Test
    void 카테고리_추가_테스트() {
        // Given
        Category mockCategory = mock(Category.class);

        // When
        member.addCategory(mockCategory);

        // Then
        assertThat(member.getCategories()).contains(mockCategory);
    }

    @Test
    void 카테고리_삭제_테스트() {
        // Given
        Category mockCategory = mock(Category.class);
        Category mockDefaultCategory = mock(Category.class);
        Task mockTask = mock(Task.class);

        when(mockCategory.getId()).thenReturn(1L);
        when(mockTask.getCategory()).thenReturn(mockCategory);

        member.addTask(mockTask);
        member.addCategory(mockCategory);

        // When
        member.deleteCategory(mockCategory, mockDefaultCategory);

        // Then
        assertThat(member.getCategories()).doesNotContain(mockCategory);
        verify(mockTask, atLeastOnce()).updateCategory(mockDefaultCategory);
    }

    @Test
    void 친구_관계_확인_테스트() {
        Member mockFriend = mock(Member.class);
        when(mockFriend.getId()).thenReturn(1L);

        member.addFriends(1L);

        assertThat(member.isFriend(mockFriend)).isTrue();
    }

    @Test
    void 팀_소속_확인_테스트() {
        Teaming mockTeaming = mock(Teaming.class);
        Team mockTeam = mock(Team.class);

        when(mockTeam.getId()).thenReturn(1L);
        when(mockTeaming.getTeam()).thenReturn(mockTeam);

        member.addTeam(mockTeaming);

        boolean isInTeam = member.inTheTeam(1L);

        assertThat(isInTeam).isTrue();
    }

    @Test
    void 초대_추가_테스트() {
        Invitation mockInvitationSent = mock(Invitation.class);
        Invitation mockInvitationReceived = mock(Invitation.class);

        Member mockSender = mock(Member.class);
        Member mockReceiver = mock(Member.class);

        when(mockSender.getEmail()).thenReturn("test@example.com");
        when(mockReceiver.getEmail()).thenReturn("receiver@example.com");

        when(mockInvitationSent.getSender()).thenReturn(mockSender);
        when(mockInvitationReceived.getSender()).thenReturn(mockReceiver);

        member.addInvitation(mockInvitationSent);
        member.addInvitation(mockInvitationReceived);

        assertThat(member.getSentInvitations()).contains(mockInvitationSent);
        assertThat(member.getReceivedInvitations()).contains(mockInvitationReceived);
    }

    @Test
    void 소속_팀_리스트업_테스트() {
        Teaming mockTeaming1 = mock(Teaming.class);
        Teaming mockTeaming2 = mock(Teaming.class);

        when(mockTeaming1.getTeamName()).thenReturn("Team A");
        when(mockTeaming2.getTeamName()).thenReturn("Team B");

        member.addTeam(mockTeaming1);
        member.addTeam(mockTeaming2);

        List<String> teamNames = member.getTeamsNames();

        assertThat(teamNames).contains("Team A", "Team B");
    }
}
