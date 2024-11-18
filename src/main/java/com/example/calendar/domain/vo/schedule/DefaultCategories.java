package com.example.calendar.domain.vo.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultCategories {
    STUDY(new CategoryInfo("study", "#FF0000")),
    WORK(new CategoryInfo("work", "#00FF00")),
    HOBBY(new CategoryInfo("hobby", "#0000FF"));

    private final CategoryInfo categoryInfo;
}

