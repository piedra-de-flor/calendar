package com.example.calendar.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {
    @Id
    @GeneratedValue
    private UUID id;
    private String email;
    private String password;

    @Getter
    private String nickName;

    @Builder
    public Member(UUID id, String name, String email, String password) {
        this.id = id;
        this.nickName = name;
        this.email = email;
        this.password = password;
    }
}
