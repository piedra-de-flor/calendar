package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.category.CategoryCreateDto;
import com.example.calendar.dto.schedule.category.CategoryDto;
import com.example.calendar.dto.schedule.category.CategoryUpdateDto;
import com.example.calendar.service.schedule.task.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/schedule/category")
    public ResponseEntity<Long> createCategory(@RequestBody CategoryCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        long response = categoryService.createCategory(memberEmail, createDto);
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

    @GetMapping("/schedule/category")
    public ResponseEntity<List<CategoryDto>> readAllCategory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<CategoryDto> response = categoryService.readAllCategory(memberEmail);
        return ResponseEntity.ok(response);
    }
}
