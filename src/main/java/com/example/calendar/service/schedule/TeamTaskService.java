package com.example.calendar.service.schedule;

import com.example.calendar.domain.entity.group.Team;
import com.example.calendar.domain.entity.group.Teaming;
import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Task;
import com.example.calendar.dto.schedule.task.freetime.AvailableTimeSlot;
import com.example.calendar.dto.schedule.task.freetime.TimeBlock;
import com.example.calendar.repository.TaskRepository;
import com.example.calendar.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamTaskService {
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    public List<AvailableTimeSlot> findAvailableTimeSlots(
            Long teamId, LocalDate startDate, LocalDate endDate,
            LocalTime availableFrom, LocalTime availableTo,
            Duration minDuration, Duration minGap, int minMembers) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find the team"));

        List<Member> members = team.getTeamings().stream()
                .map(Teaming::getMember)
                .collect(Collectors.toList());

        List<Task> tasks = taskRepository.findTasksByMembersAndDateRange(members, startDate, endDate);

        Map<Member, List<TimeBlock>> memberTimeBlocks = members.stream()
                .collect(Collectors.toMap(
                        member -> member,
                        member -> new ArrayList<>()
                ));

        tasks.forEach(task -> {
            Member member = task.getMember();
            List<TimeBlock> timeBlocks = memberTimeBlocks.get(member);

            if (timeBlocks != null) {
                timeBlocks.add(new TimeBlock(task.getDate(), task.getStartTime(), task.getEndTime()));
            }
        });

        return calculateCommonFreeSlots(memberTimeBlocks, startDate, endDate, availableFrom, availableTo, minDuration, minGap, minMembers);
    }

    private List<AvailableTimeSlot> calculateCommonFreeSlots(
            Map<Member, List<TimeBlock>> memberTimeBlocks,
            LocalDate startDate, LocalDate endDate,
            LocalTime availableFrom, LocalTime availableTo,
            Duration minDuration, Duration minGap, int minMembers) {

        List<AvailableTimeSlot> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<AvailableTimeSlot> dailySlots = calculateDailySlots(
                    memberTimeBlocks, date, availableFrom, availableTo, minDuration, minGap, minMembers);
            result.addAll(dailySlots);
        }

        return result;
    }

    private List<AvailableTimeSlot> calculateDailySlots(
            Map<Member, List<TimeBlock>> memberTimeBlocks,
            LocalDate date, LocalTime availableFrom, LocalTime availableTo,
            Duration minDuration, Duration minGap, int minMembers) {

        System.out.println("Calculating daily slots for date: " + date);

        // 병합된 TimeBlock 가져오기
        List<TimeBlock> mergedBlocks = memberTimeBlocks.values().stream()
                .flatMap(blocks -> mergeTimeBlocks(blocks).stream())
                .filter(block -> block.getDate().equals(date))
                .sorted(Comparator.comparing(TimeBlock::getStartTime))
                .toList();

        List<AvailableTimeSlot> availableSlots = new ArrayList<>();
        LocalTime lastEndTime = availableFrom;

        if (mergedBlocks.isEmpty()) {
            // 병합된 블록이 없을 경우 전체 가능 시간 추가
            List<String> availableMembers = memberTimeBlocks.keySet().stream()
                    .map(Member::getName)
                    .collect(Collectors.toList());

            if (availableMembers.size() >= minMembers) {
                availableSlots.add(new AvailableTimeSlot(
                        LocalDateTime.of(date, availableFrom),
                        LocalDateTime.of(date, availableTo),
                        availableMembers
                ));
            }
            return availableSlots;
        }

        for (TimeBlock block : mergedBlocks) {
            LocalTime potentialStart = lastEndTime.plus(minGap);

            // 시간 경계 처리
            if (potentialStart.isBefore(block.getStartTime()) && potentialStart.isBefore(availableTo)) {
                LocalTime potentialEnd = block.getStartTime();
                if (potentialEnd.isAfter(availableTo)) {
                    potentialEnd = availableTo;
                }

                // 최소 지속 시간 조건 확인
                if (Duration.between(potentialStart, potentialEnd).compareTo(minDuration) >= 0) {
                    List<String> availableMembers = findAvailableMembers(memberTimeBlocks, date, potentialStart, potentialEnd);
                    System.out.println("Available Members: " + availableMembers);

                    if (availableMembers.size() >= minMembers) {
                        availableSlots.add(new AvailableTimeSlot(
                                LocalDateTime.of(date, potentialStart),
                                LocalDateTime.of(date, potentialEnd),
                                availableMembers
                        ));
                    }
                }
            }

            // 마지막 종료 시간 갱신
            lastEndTime = block.getEndTime().isAfter(lastEndTime) ? block.getEndTime() : lastEndTime;
        }

        // 하루의 마지막 여유 시간 처리
        LocalTime potentialStart = lastEndTime.plus(minGap);
        if (potentialStart.isBefore(availableTo)) {
            if (Duration.between(potentialStart, availableTo).compareTo(minDuration) >= 0) {
                List<String> availableMembers = findAvailableMembers(memberTimeBlocks, date, potentialStart, availableTo);
                System.out.println("Available Members for end of day: " + availableMembers);

                if (availableMembers.size() >= minMembers) {
                    availableSlots.add(new AvailableTimeSlot(
                            LocalDateTime.of(date, potentialStart),
                            LocalDateTime.of(date, availableTo),
                            availableMembers
                    ));
                }
            }
        }

        return availableSlots;
    }

    private List<String> findAvailableMembers(
            Map<Member, List<TimeBlock>> memberTimeBlocks,
            LocalDate date, LocalTime startTime, LocalTime endTime) {

        return memberTimeBlocks.entrySet().stream()
                .filter(entry -> entry.getValue().stream().noneMatch(block ->
                        block.getDate().equals(date) &&
                                (block.getStartTime().isBefore(endTime) && block.getEndTime().isAfter(startTime))
                ))
                .map(Map.Entry::getKey)
                .map(Member::getName)
                .collect(Collectors.toList());
    }

    private List<TimeBlock> mergeTimeBlocks(List<TimeBlock> blocks) {
        if (blocks.isEmpty()) {
            return new ArrayList<>();
        }

        List<TimeBlock> sortedBlocks = blocks.stream()
                .sorted(Comparator.comparing(TimeBlock::getStartTime))
                .toList();

        List<TimeBlock> mergedBlocks = new ArrayList<>();
        TimeBlock current = sortedBlocks.get(0);

        for (int i = 1; i < sortedBlocks.size(); i++) {
            TimeBlock next = sortedBlocks.get(i);

            if (!current.getEndTime().isBefore(next.getStartTime())) {
                current = new TimeBlock(
                        current.getDate(),
                        current.getStartTime(),
                        current.getEndTime().isAfter(next.getEndTime()) ? current.getEndTime() : next.getEndTime()
                );
            } else {
                mergedBlocks.add(current);
                current = next;
            }
        }

        mergedBlocks.add(current);
        return mergedBlocks;
    }
}
