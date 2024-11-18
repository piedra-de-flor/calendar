package com.example.calendar.domain.vo.schedule;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class CategoryInfo {
    private String name;
    private String color;

    @Builder
    public CategoryInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
