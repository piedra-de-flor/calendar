package com.example.calendar.domain.entity.schedule;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.vo.schedule.CategoryInfo;
import com.example.calendar.domain.vo.schedule.Color;
import com.example.calendar.dto.schedule.category.CategoryUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryTest {
    private Category category;
    private CategoryInfo mockCategoryInfo;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        mockCategoryInfo = mock(CategoryInfo.class);
        mockMember = mock(Member.class);

        // Category 초기화
        category = Category.builder()
                .member(mockMember)
                .categoryInfo(mockCategoryInfo)
                .build();
    }

    @Test
    void 카테고리_이름_가져오기_테스트() {
        // Given
        when(mockCategoryInfo.getName()).thenReturn("Work");

        // When
        String categoryName = category.getCategoryName();

        // Then
        assertThat(categoryName).isEqualTo("Work");
        verify(mockCategoryInfo, atLeastOnce()).getName();
    }

    @Test
    void 카테고리_색상_가져오기_테스트() {
        // Given
        when(mockCategoryInfo.getColor()).thenReturn("#FFFFFF");

        // When
        String categoryColor = category.getCategoryColor();

        // Then
        assertThat(categoryColor).isEqualTo("#FFFFFF");
        verify(mockCategoryInfo, atLeastOnce()).getColor();
    }

    @Test
    void 카테고리_업데이트_테스트() {
        // Given
        CategoryUpdateDto mockUpdateDto = mock(CategoryUpdateDto.class);
        when(mockUpdateDto.name()).thenReturn("Updated Name");
        when(mockUpdateDto.color()).thenReturn("BLUE");

        // When
        category.update(mockUpdateDto);

        // Then
        verify(mockCategoryInfo, atLeastOnce()).update("Updated Name", Color.valueOf("BLUE").getCode());
    }
}
