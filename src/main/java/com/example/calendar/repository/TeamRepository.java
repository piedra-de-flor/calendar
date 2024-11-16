package com.example.calendar.repository;

import com.example.calendar.domain.entity.group.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByTeamingsEmpty();
}

