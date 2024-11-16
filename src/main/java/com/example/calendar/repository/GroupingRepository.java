package com.example.calendar.repository;

import com.example.calendar.domain.entity.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupingRepository extends JpaRepository<Grouping, Long> {
}
