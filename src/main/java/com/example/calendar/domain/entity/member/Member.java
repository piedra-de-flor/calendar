package com.example.calendar.domain.entity.member;

import com.example.calendar.domain.entity.group.Grouping;
import com.example.calendar.domain.entity.invitation.Invitation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    private String provider;
    private String providerId;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> friends = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Grouping> groupings = new ArrayList<>();;

    @OneToMany(mappedBy = "member")
    private List<Invitation> invitations = new ArrayList<>();

    @Builder
    public Member(String name, String email, String password, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles.add("USER");
        this.provider = provider;
        this.providerId = providerId;
    }

    public void update(String name, String password) {
        if (name != null) {
            this.name = name;
        }

        if (password != null) {
            this.password = password;
        }
    }

    public void addFriends(long friendId) {
        this.friends.add(friendId);
    }

    public void deleteFriends(long friendId) {
        this.friends.remove(friendId);
    }

    public void addGroup(Grouping grouping) {
        this.groupings.add(grouping);
    }

    public void exitGroup(Grouping grouping) {
        this.groupings.remove(grouping);
    }

    public void addInvitation(Invitation invitation) {
        this.invitations.add(invitation);
    }

    public List<String> getGroupsNames() {
        List<String> names = new ArrayList<>();

        for (Grouping grouping : groupings) {
            names.add(grouping.getGroupName());
        }

        return names;
    }
}
