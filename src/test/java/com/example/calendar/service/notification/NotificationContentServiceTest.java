package com.example.calendar.service.notification;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.vote.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationContentServiceTest {

    private NotificationContentService notificationContentService;

    @BeforeEach
    void setUp() {
        notificationContentService = new NotificationContentService();
    }

    @Test
    void 친구_초대_알림_메세지_테스트() {
        // Given
        Member sender = mock(Member.class);
        when(sender.getName()).thenReturn("John");

        // When
        String result = notificationContentService.makeFriendInvitationContent(sender);

        // Then
        assertThat(result).isEqualTo("John님이 친구를 요청하셨습니다.");
    }

    @Test
    void 팀_초대_알림_메세지_테스트() {
        // Given
        Member sender = mock(Member.class);
        Team team = mock(Team.class);
        when(sender.getName()).thenReturn("Alice");
        when(team.getName()).thenReturn("Dev Team");

        // When
        String result = notificationContentService.inviteTeam(sender, team);

        // Then
        assertThat(result).isEqualTo("Alice님이 Dev Team 팀에 초대를 했습니다.");
    }

    @Test
    void 초대_수락_알림_메세지_테스트() {
        // Given
        Member receiver = mock(Member.class);
        when(receiver.getName()).thenReturn("Bob");

        // When
        String result = notificationContentService.acceptInvitation(receiver);

        // Then
        assertThat(result).isEqualTo("Bob님이 요청을 수락하셨습니다.");
    }

    @Test
    void 투표_생성_알림_메세지_테스트() {
        // Given
        Team team = mock(Team.class);
        when(team.getName()).thenReturn("Marketing Team");

        // When
        String result = notificationContentService.createVote(team);

        // Then
        assertThat(result).isEqualTo("Marketing Team팀에서 투표가 생성되었습니다!");
    }

    @Test
    void 투표_완료_알림_메세지_테스트() {
        // Given
        Team team = mock(Team.class);
        Vote vote = mock(Vote.class);

        when(vote.getTeam()).thenReturn(team);
        when(vote.getTitle()).thenReturn("Project Approval");
        when(team.getName()).thenReturn("Design Team");

        // When
        String result = notificationContentService.completeVote(vote);

        // Then
        assertThat(result).isEqualTo("Design Team팀에서 Project Approval 투표가 종료되었습니다.");
    }
}
