package com.example.calendar.repository;

import com.example.calendar.domain.entity.schedule.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
