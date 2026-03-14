package page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageInfoTest {

    @Test
    @DisplayName("of() 팩토리 메서드로 페이지 정보 정상 생성")
    void of_정상_생성() {
        List<String> content = List.of("a", "b", "c");

        PageInfo<List<String>> pageInfo = PageInfo.of(content, 1, 10, 25L, 3, 3);

        assertThat(pageInfo.getContent()).isEqualTo(content);
        assertThat(pageInfo.getPageNum()).isEqualTo(1);
        assertThat(pageInfo.getPageSize()).isEqualTo(10);
        assertThat(pageInfo.getTotalCount()).isEqualTo(25L);
        assertThat(pageInfo.getTotalPage()).isEqualTo(3);
        assertThat(pageInfo.getElementSize()).isEqualTo(3);
    }
}
