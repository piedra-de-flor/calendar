package com.example.calendar.domain.entity.group;

import com.example.calendar.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "teaming")
public class Teaming {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    public Teaming(Member member, Team team) {
        this.member = member;
        this.team = team;
    }

    public String getTeamName() {
        return team.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teaming teaming = (Teaming) o;
        return Objects.equals(member, teaming.member) &&
                Objects.equals(team, teaming.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, team);
    }
}
