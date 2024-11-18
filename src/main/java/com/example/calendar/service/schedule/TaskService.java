package com.example.calendar.service.schedule;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.dto.schedule.TaskCreateDto;
import com.example.calendar.repository.CategoryRepository;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public long createTask(String memberEmail, TaskCreateDto taskCreateDto) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Optional<Task> oldTask = member.getTasks().stream()
                .filter(task -> task.getStartTime().isBefore(taskCreateDto.endTime()) ||
                        task.getEndTime().isAfter(taskCreateDto.startTime()))
                .findFirst();

        oldTask.ifPresent(taskRepository::delete);

        Category category = categoryRepository.findById(taskCreateDto.categoryId())
                .orElseThrow(NoSuchElementException::new);

        Task task = Task.builder()
                .member(member)
                .category(category)
                .date(taskCreateDto.date())
                .startTime(taskCreateDto.startTime())
                .endTime(taskCreateDto.endTime())
                .description(taskCreateDto.description())
                .build();

        Task savedTask = taskRepository.save(task);
        member.addTask(task);

        return savedTask.getId();
    }
}
