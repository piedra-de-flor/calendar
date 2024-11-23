package com.example.calendar.repository;

import com.example.calendar.domain.entity.vote.Vote;
import com.example.calendar.domain.vo.vote.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAllByStatus(VoteStatus status);
}
