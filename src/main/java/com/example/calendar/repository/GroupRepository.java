package com.example.calendar.repository;

import com.example.calendar.domain.entity.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByGroupingsEmpty();
}
