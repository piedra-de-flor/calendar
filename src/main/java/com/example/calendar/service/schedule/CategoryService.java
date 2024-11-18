package com.example.calendar.service.schedule;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.vo.schedule.CategoryInfo;
import com.example.calendar.domain.vo.schedule.Color;
import com.example.calendar.dto.schedule.category.CategoryCreateDto;
import com.example.calendar.repository.CategoryRepository;
import com.example.calendar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean createCategory(String memberEmail, CategoryCreateDto createDto) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        if (member.getCategories().stream()
                .anyMatch(category -> category.getCategoryName().equals(createDto.name()))) {
            throw new IllegalArgumentException("it's duplicated category name");
        }

        Color color = Color.valueOf(createDto.color());
        CategoryInfo categoryInfo = new CategoryInfo(createDto.name(), color.getCode());
        Category category = Category.builder()
                .member(member)
                .categoryInfo(categoryInfo)
                .build();

        categoryRepository.save(category);
        member.addCategory(category);
        return true;
    }

    @Transactional
    public boolean deleteCategory(String memberEmail, long categoryId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(NoSuchElementException::new);

        Category target = categoryRepository.findById(categoryId)
                .orElseThrow(NoSuchElementException::new);

        if (member.getId() == target.getMember().getId()) {
            categoryRepository.delete(target);
            return true;
        }

        throw new IllegalArgumentException("you don't have auth to delete this category");
    }
}
