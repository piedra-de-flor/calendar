package com.example.calendar.service.schedule;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.dto.schedule.DailyTaskDto;
import com.example.calendar.dto.schedule.MonthlyTaskDto;
import com.example.calendar.dto.schedule.TaskCreateDto;
import com.example.calendar.dto.schedule.TaskDto;
import com.example.calendar.repository.CategoryRepository;
import com.example.calendar.repository.MemberRepository;
import com.example.calendar.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public boolean deleteTask(String memberEmail, long taskId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Task target = taskRepository.findById(taskId)
                .orElseThrow(NoSuchElementException::new);

        if (target.getMember().getId() == member.getId()) {
            taskRepository.delete(target);
            return true;
        }

        throw new IllegalArgumentException("you don't have auth to delete task");
    }

    public DailyTaskDto readDailyTasks(String memberEmail, LocalDate date) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        return new DailyTaskDto(getTodayTasks(member, date));
    }

    public MonthlyTaskDto readMonthlyTasks(String memberEmail, LocalDate startDate, LocalDate endDate) {
        List<DailyTaskDto> dailyTaskDtos = new ArrayList<>();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        LocalDate date = startDate;
        while (date.isBefore(endDate)) {
            DailyTaskDto day = new DailyTaskDto(getTodayTasks(member, date));
            dailyTaskDtos.add(day);
            date = date.plusDays(1);
        }

        return new MonthlyTaskDto(dailyTaskDtos);
    }

    public TaskDto readTask(String memberEmail, long taskId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getId() == task.getMember().getId()) {
            return new TaskDto(
                    task.getCategory().getCategoryName(),
                    task.getCategory().getCategoryColor(),
                    task.getStartTime(),
                    task.getEndTime(),
                    task.getDescription()
            );
        }

        throw new IllegalArgumentException("you don't have auth to read this task");
    }

    private List<TaskDto> getTodayTasks(Member member, LocalDate date) {
        List<Task> todayTasks = member.getTasks().stream()
                .filter(task -> task.getDate().equals(date))
                .toList();

        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : todayTasks) {
            taskDtos.add(new TaskDto(
                    task.getCategory().getCategoryName(),
                    task.getCategory().getCategoryColor(),
                    task.getStartTime(),
                    task.getEndTime(),
                    task.getDescription()
            ));
        }

        return taskDtos;
    }
}
