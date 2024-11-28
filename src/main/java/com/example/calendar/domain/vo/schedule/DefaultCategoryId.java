package com.example.calendar.domain.vo.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultCategoryId {
    EMPTY_CATEGORY_ID(1L);

    private final long value;
}
