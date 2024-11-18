package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.TaskCreateDto;
import com.example.calendar.dto.schedule.TaskDto;
import com.example.calendar.service.schedule.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/schedule/task")
    public ResponseEntity<List<TaskDto>> readTask(@RequestBody LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        List<TaskDto> response = taskService.readTask(memberEmail, date);
        return ResponseEntity.ok(response);
    }
}
