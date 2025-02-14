package com.example.calendar.repository;

import com.example.calendar.domain.entity.schedule.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
