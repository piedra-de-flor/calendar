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
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "group")
    private List<Grouping> groupings = new ArrayList<>();

    @Builder
    public Group(String name) {
        this.name = name;
    }

    public void addGrouping(Grouping grouping) {
        groupings.add(grouping);
    }
}
