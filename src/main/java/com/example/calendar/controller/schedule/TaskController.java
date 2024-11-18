package com.example.calendar.controller.schedule;

import com.example.calendar.dto.schedule.TaskCreateDto;
import com.example.calendar.service.schedule.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/schedule/task")
    public ResponseEntity<Long> createFriend(@RequestParam TaskCreateDto taskCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = authentication.getName();

        long response = taskService.createTask(memberEmail, taskCreateDto);
        return ResponseEntity.ok(response);
    }
}
