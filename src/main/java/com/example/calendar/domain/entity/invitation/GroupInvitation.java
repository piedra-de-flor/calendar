package com.example.calendar.domain.entity.invitation;

import com.example.calendar.domain.entity.group.Group;
import com.example.calendar.domain.entity.group.Grouping;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.invitation.InvitationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DiscriminatorValue("GROUP")
public class GroupInvitation extends Invitation {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToOne(mappedBy = "invitation")
    private Grouping grouping;

    @Builder
    public GroupInvitation(Member receiver, Member sender, Group group, Grouping grouping) {
        super(receiver, sender);
        this.group = group;
        this.grouping = grouping;
    }

    @Override
    protected void acceptHandle() {
        group.addGrouping(grouping);
        super.getReceiver().addGroup(grouping);
    }
}
