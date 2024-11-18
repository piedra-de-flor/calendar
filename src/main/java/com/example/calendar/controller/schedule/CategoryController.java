package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.category.CategoryCreateDto;
import com.example.calendar.dto.schedule.category.CategoryUpdateDto;
import com.example.calendar.service.schedule.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/schedule/category")
    public ResponseEntity<Boolean> deleteCategory(@RequestParam long categoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = categoryService.deleteCategory(memberEmail, categoryId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/schedule/category")
    public ResponseEntity<Boolean> updateCategory(@RequestBody CategoryUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = categoryService.updateCategory(memberEmail, updateDto);
        return ResponseEntity.ok(response);
    }
}
