package com.example.calendar.domain.entity.vote;

import com.example.calendar.domain.entity.group.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    }
}
