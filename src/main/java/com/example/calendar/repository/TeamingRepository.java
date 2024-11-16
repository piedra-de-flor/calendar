package com.example.calendar.repository;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamingRepository extends JpaRepository<Teaming, Long> {
    Optional<Teaming> findByMemberAndTeam(Member member, Team team);
}
