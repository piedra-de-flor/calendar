package com.example.calendar.domain.vo.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Color {
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF"),
    YELLOW("#FFFF00"),
    SKYBLUE("#CCEEFF"),
    GREY("#F8F9FA");

    private final String code;
}
