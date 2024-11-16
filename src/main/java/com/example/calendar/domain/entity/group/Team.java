package com.example.calendar.domain.entity.group;

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
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Teaming> teamings = new ArrayList<>();

    @Builder
    public Team(String name) {
        this.name = name;
    }

    public void addGrouping(Teaming teaming) {
        teamings.add(teaming);
    }
}
