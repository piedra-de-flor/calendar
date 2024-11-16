package com.example.calendar.repository;

import com.example.calendar.domain.entity.Group;
import com.example.calendar.domain.entity.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByGroupingsEmpty();
}
