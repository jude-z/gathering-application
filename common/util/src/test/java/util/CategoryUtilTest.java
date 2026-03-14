package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryUtilTest {

    @Test
    @DisplayName("existCategory - 존재하는 카테고리는 true 반환")
    void existCategory_존재하는_카테고리() {
        assertThat(CategoryUtil.existCategory("category1")).isTrue();
        assertThat(CategoryUtil.existCategory("category10")).isTrue();
        assertThat(CategoryUtil.existCategory("category20")).isTrue();
    }

    @Test
    @DisplayName("existCategory - 존재하지 않는 카테고리는 false 반환")
    void existCategory_존재하지_않는_카테고리() {
        assertThat(CategoryUtil.existCategory("unknown")).isFalse();
        assertThat(CategoryUtil.existCategory("category0")).isFalse();
        assertThat(CategoryUtil.existCategory("category21")).isFalse();
    }

    @Test
    @DisplayName("names 배열 크기는 20개")
    void names_배열_크기_확인() {
        assertThat(CategoryUtil.names).hasSize(20);
    }
}
