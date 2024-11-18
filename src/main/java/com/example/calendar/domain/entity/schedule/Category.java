package com.example.calendar.domain.entity.schedule;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.schedule.CategoryInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private CategoryInfo categoryInfo;

    @ManyToOne
    @JoinColumn(name = "member")
    private Member member;

    @Builder
    public Category(Member member, CategoryInfo categoryInfo) {
        this.member = member;
        this.categoryInfo = categoryInfo;
    }

    public String getCategoryName() {
        return categoryInfo.getName();
    }

    public String getCategoryColor() {
        return categoryInfo.getColor();
    }
}
