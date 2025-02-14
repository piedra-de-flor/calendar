package com.example.calendar.service.schedule.task;

import com.example.calendar.domain.entity.member.Member;
import com.example.calendar.domain.entity.schedule.Category;
import com.example.calendar.domain.vo.schedule.DefaultCategoryId;
import com.example.calendar.dto.schedule.category.CategoryCreateDto;
import com.example.calendar.dto.schedule.category.CategoryDto;
import com.example.calendar.dto.schedule.category.CategoryUpdateDto;
import com.example.calendar.repository.CategoryRepository;
import com.example.calendar.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 카테고리_생성_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        CategoryCreateDto createDto = new CategoryCreateDto("Category", "RED");

        Member member = mock(Member.class);
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getCategories()).thenReturn(new ArrayList<>());
        Category savedCategory = mock(Category.class);
        when(savedCategory.getId()).thenReturn(1L);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        long result = categoryService.createCategory(memberEmail, createDto);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(memberRepository, times(1)).findByEmail(memberEmail);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(member, times(1)).addCategory(any(Category.class));
    }

    @Test
    void 카테고리_생성중_중복_이름_예외_테스트() {
        // Given
        String memberEmail = "user@example.com";
        CategoryCreateDto createDto = new CategoryCreateDto("Duplicate", "RED");

        Member member = mock(Member.class);
        Category existingCategory = mock(Category.class);
        when(existingCategory.getCategoryName()).thenReturn("Duplicate");
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getCategories()).thenReturn(List.of(existingCategory));

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(memberEmail, createDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("it's duplicated category name");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void 카테고리_삭제_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long categoryId = 2L;

        // Mock Member and Category
        Member member = mock(Member.class);
        Category targetCategory = mock(Category.class);
        Category defaultCategory = mock(Category.class);

        // Mock repository behaviors
        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(targetCategory));
        when(categoryRepository.findById(DefaultCategoryId.EMPTY_CATEGORY_ID.getValue())).thenReturn(Optional.of(defaultCategory));

        // Mock member and category relationships
        when(member.getId()).thenReturn(1L);
        when(targetCategory.getMember()).thenReturn(member);
        when(defaultCategory.getMember()).thenReturn(member);

        // When
        boolean result = categoryService.deleteCategory(memberEmail, categoryId);

        // Then
        assertThat(result).isTrue();
        verify(categoryRepository, times(1)).delete(targetCategory);
        verify(member, times(1)).deleteCategory(targetCategory, defaultCategory);
    }

    @Test
    void 카테고리_삭제_권한_예외_테스트() {
        // Given
        String memberEmail = "user@example.com";
        long categoryId = 1L;

        Member member = mock(Member.class);
        Member anotherMember = mock(Member.class);
        Category targetCategory = mock(Category.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(targetCategory));
        when(targetCategory.getMember()).thenReturn(anotherMember);
        when(member.getId()).thenReturn(1L);
        when(anotherMember.getId()).thenReturn(2L);

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(memberEmail, categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("you don't have auth to delete this category");

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void 카테고리_수정_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        CategoryUpdateDto updateDto = new CategoryUpdateDto(1L, "Updated", "BLUE");

        Member member = mock(Member.class);
        Category category = mock(Category.class);

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(categoryRepository.findById(updateDto.categoryId())).thenReturn(Optional.of(category));
        when(category.getMember()).thenReturn(member);

        // When
        boolean result = categoryService.updateCategory(memberEmail, updateDto);

        // Then
        assertThat(result).isTrue();
        verify(category, times(1)).update(updateDto);
    }

    @Test
    void 카테고리_전체_조회_성공_테스트() {
        // Given
        String memberEmail = "user@example.com";
        Member member = mock(Member.class);

        Category category1 = mock(Category.class);
        when(category1.getId()).thenReturn(1L);
        when(category1.getCategoryName()).thenReturn("Category 1");
        when(category1.getCategoryColor()).thenReturn("RED");

        Category category2 = mock(Category.class);
        when(category2.getId()).thenReturn(2L);
        when(category2.getCategoryName()).thenReturn("Category 2");
        when(category2.getCategoryColor()).thenReturn("BLUE");

        when(memberRepository.findByEmail(memberEmail)).thenReturn(Optional.of(member));
        when(member.getCategories()).thenReturn(List.of(category1, category2));

        // When
        List<CategoryDto> result = categoryService.readAllCategory(memberEmail);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategoryDto::name).containsExactlyInAnyOrder("Category 1", "Category 2");
    }
}
