package page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScrollInfoTest {

    @Test
    @DisplayName("of() - 다음 페이지가 있는 경우 hasNext true")
    void of_다음_페이지_있음() {
        ScrollInfo scrollInfo = ScrollInfo.of(1, 10, true);

        assertThat(scrollInfo.getPageNum()).isEqualTo(1);
        assertThat(scrollInfo.getPageSize()).isEqualTo(10);
        assertThat(scrollInfo.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("of() - 다음 페이지가 없는 경우 hasNext false")
    void of_다음_페이지_없음() {
        ScrollInfo scrollInfo = ScrollInfo.of(3, 10, false);

        assertThat(scrollInfo.getPageNum()).isEqualTo(3);
        assertThat(scrollInfo.getPageSize()).isEqualTo(10);
        assertThat(scrollInfo.isHasNext()).isFalse();
    }
}
