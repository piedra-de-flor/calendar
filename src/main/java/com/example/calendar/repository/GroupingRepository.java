package com.example.calendar.repository;

import com.example.calendar.domain.entity.group.Group;
import com.example.calendar.domain.entity.group.Grouping;
import com.example.calendar.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupingRepository extends JpaRepository<Grouping, Long> {
    Optional<Grouping> findByMemberAndGroup(Member member, Group group);
}
