package com.example.calendar.domain.entity.group;

import com.example.calendar.domain.entity.group.Group;
import com.example.calendar.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Grouping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public Grouping(Member member, Group group) {
        this.member = member;
        this.group = group;
    }

    public String getGroupName() {
        return group.getName();
    }
}
