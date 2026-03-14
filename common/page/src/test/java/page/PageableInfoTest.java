package page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageableInfoTest {

    @Test
    @DisplayName("of() 팩토리 메서드로 offset, limit 정상 생성")
    void of_정상_생성() {
        PageableInfo pageableInfo = PageableInfo.of(5, 10);

        assertThat(pageableInfo.getOffset()).isEqualTo(5);
        assertThat(pageableInfo.getLimit()).isEqualTo(10);
    }
}
