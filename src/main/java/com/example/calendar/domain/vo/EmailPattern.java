package com.example.calendar.domain.vo;

import java.util.regex.Pattern;

public class EmailPattern {
    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
}
