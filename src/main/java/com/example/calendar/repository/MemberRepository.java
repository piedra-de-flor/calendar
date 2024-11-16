package com.example.calendar.repository;

import com.example.calendar.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
}
