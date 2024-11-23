package com.example.calendar.repository;

import com.example.calendar.domain.entity.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
