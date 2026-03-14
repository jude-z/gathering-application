package entity.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    @DisplayName("Builder로 Category 정상 생성")
    void builder_정상_생성() {
        Category category = Category.builder()
                .name("category1")
                .build();

        assertThat(category.getName()).isEqualTo("category1");
    }

    @Test
    @DisplayName("changeName() 호출 시 카테고리명 변경")
    void changeName_이름_변경() {
        Category category = Category.builder()
                .name("oldName")
                .build();

        category.changeName("newName");

        assertThat(category.getName()).isEqualTo("newName");
    }
}
