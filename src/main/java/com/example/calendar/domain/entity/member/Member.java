package com.example.calendar.domain.entity.member;

import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.invitation.Invitation;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.Task;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String password;
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    private String provider;
    private String providerId;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> friends = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Teaming> teamings = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Invitation> receivedInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Invitation> sentInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

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

    public void addTeam(Teaming teaming) {
        this.teamings.add(teaming);
    }

    public void exitTeam(Teaming teaming) {
        this.teamings.remove(teaming);
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public void deleteCategory(Category category, Category defaultCategory) {
        tasks.stream()
                .filter(task -> Objects.equals(task.getCategory().getId(), category.getId()))
                .forEach(task -> task.updateCategory(defaultCategory));

        this.categories.remove(category);
    }

    public boolean inTheTeam(long teamId) {
        return teamings.stream()
                .anyMatch(teaming -> teaming.getTeam().getId() == teamId);
    }

    public boolean isFriend(Member friend) {
        return friends.stream()
                .anyMatch(aLong -> Objects.equals(aLong, friend.getId()));
    }

    public void addInvitation(Invitation invitation) {
        String senderEmail = invitation.getSender().getEmail();
        if (senderEmail.equals(this.email)) {
            sentInvitations.add(invitation);
        } else {
            receivedInvitations.add(invitation);
        }
    }

    public List<String> getTeamsNames() {
        List<String> names = new ArrayList<>();

        for (Teaming teaming : teamings) {
            names.add(teaming.getTeamName());
        }

        return names;
    }
}
