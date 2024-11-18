package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.category.CategoryCreateDto;
import com.example.calendar.dto.schedule.task.TaskCreateDto;
import com.example.calendar.service.schedule.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/schedule/category")
    public ResponseEntity<Boolean> createTask(@RequestBody CategoryCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = categoryService.createCategory(memberEmail, createDto);
        return ResponseEntity.ok(response);
    }
}
