package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.task.*;
import com.example.calendar.service.schedule.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/schedule/task")
    public ResponseEntity<Long> createTask(@RequestBody TaskCreateDto taskCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        long response = taskService.createTask(memberEmail, taskCreateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/schedule/task")
    public ResponseEntity<Boolean> deleteTask(@RequestParam long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        boolean response = taskService.deleteTask(memberEmail, taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/task/day")
    public ResponseEntity<DailyTaskDto> readDayTask(@RequestBody LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        DailyTaskDto response = taskService.readDailyTasks(memberEmail, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/task/month")
    public ResponseEntity<MonthlyTaskDto> readMonthTasks(@RequestBody LocalDate startDate, LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        MonthlyTaskDto response = taskService.readMonthlyTasks(memberEmail, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/task")
    public ResponseEntity<TaskDto> readDayTask(@RequestParam long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        TaskDto response = taskService.readTask(memberEmail, taskId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/schedule/task")
    public ResponseEntity<Long> updateTask(@RequestBody TaskUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        long response = taskService.updateTask(memberEmail, updateDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/schedule/task/category")
    public ResponseEntity<Long> updateTaskCategory(@RequestBody TaskCategoryUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        long response = taskService.updateTaskCategory(memberEmail, updateDto);
        return ResponseEntity.ok(response);
    }
}
