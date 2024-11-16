package com.example.calendar.domain.entity.invitation;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DiscriminatorValue("GROUP")
public class TeamInvitation extends Invitation {
    @ManyToOne
    private Team team;

    @OneToOne
    private Teaming teaming;

    public TeamInvitation(Member receiver, Member sender, Team team, Teaming teaming) {
        super(sender, receiver);
        this.team = team;
        this.teaming = teaming;
    }

    @Override
    protected void acceptHandle() {
        team.addTeaming(teaming);
        super.getReceiver().addTeam(teaming);
    }
}
