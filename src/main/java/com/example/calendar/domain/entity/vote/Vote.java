package com.example.calendar.domain.entity.vote;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.vo.vote.VoteStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;
    private boolean isMultipleChoice;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options;

    @Enumerated(EnumType.STRING)
    private VoteStatus status = VoteStatus.OPEN;

    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    public Vote(Team team, String title, String description, boolean isMultipleChoice, List<VoteOption> options) {
        this.team = team;
        this.title = title;
        this.description = description;
        this.isMultipleChoice = isMultipleChoice;
        this.options = options;
        this.createdAt = LocalDateTime.now();
        this.closedAt = LocalDateTime.now().plusDays(3);
    }

    public boolean isOpen() {
        return this.status == VoteStatus.OPEN;
    }

    public void close() {
        if (this.status == VoteStatus.CLOSED) {
            throw new IllegalStateException("The vote is already closed.");
        }
        this.status = VoteStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public List<VoteOption> castedOptions(String email) {
        List<VoteOption> response = new ArrayList<>();

        for (VoteOption option : options) {
            if (option.isCasted(email)) {
                response.add(option);
            }
        }

        return response;
    }
}
