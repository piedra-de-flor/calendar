package com.example.calendar.dto.schedule.category;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryUpdateDtoTest {

    @Test
    void 모든_필드가_유효한_경우_생성_성공_테스트() {
        // Given
        long categoryId = 1L;
        String name = "ValidName";
        String color = "#FFFFFF";

        // When
        CategoryUpdateDto dto = new CategoryUpdateDto(categoryId, name, color);

        // Then
        assertThat(dto.categoryId()).isEqualTo(categoryId);
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.color()).isEqualTo(color);
    }

    @Test
    void 이름이_null일_경우_예외_테스트() {
        // Given
        long categoryId = 1L;
        String color = "#FFFFFF";

        // When & Then
        assertThatThrownBy(() -> new CategoryUpdateDto(categoryId, null, color))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name must not be null");
    }

    @Test
    void 색상이_null일_경우_예외_테스트() {
        // Given
        long categoryId = 1L;
        String name = "ValidName";

        // When & Then
        assertThatThrownBy(() -> new CategoryUpdateDto(categoryId, name, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("You should choose one color");
    }

    @Test
    void 이름_길이가_10자_이상일_경우_예외_테스트() {
        // Given
        long categoryId = 1L;
        String name = "VeryLongName"; // 10자 이상
        String color = "#FFFFFF";

        // When & Then
        assertThatThrownBy(() -> new CategoryUpdateDto(categoryId, name, color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must be smaller than 10 character");
    }

    @Test
    void 이름이_공백으로_시작하거나_끝날_경우_예외_테스트() {
        // Given
        long categoryId = 1L;
        String color = "#FFFFFF";

        // When & Then
        assertThatThrownBy(() -> new CategoryUpdateDto(categoryId, " Invalid", color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not start or end with blank");

        assertThatThrownBy(() -> new CategoryUpdateDto(categoryId, "Invalid ", color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not start or end with blank");
    }
}
