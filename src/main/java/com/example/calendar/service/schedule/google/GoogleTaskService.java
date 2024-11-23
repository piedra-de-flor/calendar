package com.example.calendar.service.schedule.google;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.GoogleCalendarTask;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.dto.schedule.google.GoogleTaskCreateDto;
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
public class GoogleTaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createGoogleTask(String memberEmail, GoogleTaskCreateDto taskCreateDto) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Category category = categoryRepository.findById(taskCreateDto.categoryId())
                .orElseThrow(NoSuchElementException::new);

        Optional<GoogleCalendarTask> existingTask = member.getTasks().stream()
                .filter(task -> task instanceof GoogleCalendarTask)
                .map(task -> (GoogleCalendarTask) task)
                .filter(googleTask -> googleTask.getEventId().equals(taskCreateDto.eventId()))
                .findFirst();

        if (existingTask.isPresent()) {
            GoogleCalendarTask currentTask = existingTask.get();

            if (currentTask.getDate().equals(taskCreateDto.date()) &&
                    currentTask.getStartTime().equals(taskCreateDto.startTime()) &&
                    currentTask.getEndTime().equals(taskCreateDto.endTime())) {
                return;
            }

            taskRepository.delete(currentTask);
            member.deleteTask(currentTask);
        }

        Task newTask = new GoogleCalendarTask(
                member,
                category,
                taskCreateDto.date(),
                taskCreateDto.startTime(),
                taskCreateDto.endTime(),
                taskCreateDto.description(),
                taskCreateDto.eventId());

        taskRepository.save(newTask);
        member.addTask(newTask);
    }
}
