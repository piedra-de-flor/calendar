package com.example.calendar.domain.entity.group;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeamTest {
    @Test
    void 팀_추가_테스트() {
        Team team = new Team("test");
        Teaming mockTeaming = mock(Teaming.class);

        when(mockTeaming.getTeam()).thenReturn(team);

        team.addTeaming(mockTeaming);

        Set<Teaming> teamings = team.getTeamings();

        assertThat(teamings).contains(mockTeaming);
        assertThat(teamings.size()).isEqualTo(1);
    }
}
