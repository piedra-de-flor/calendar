package com.example.calendar.repository;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.member IN :members AND t.date BETWEEN :startDate AND :endDate")
    List<Task> findTasksByMembersAndDateRange(@Param("members") List<Member> members,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}