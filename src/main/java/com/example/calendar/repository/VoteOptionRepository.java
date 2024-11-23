package com.example.calendar.repository;

import com.example.calendar.domain.entity.vote.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
}
