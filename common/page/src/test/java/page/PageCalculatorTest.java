package page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageCalculatorTest {

    @Test
    @DisplayName("toPageableInfo - 첫 페이지 offset은 0")
    void toPageableInfo_첫_페이지() {
        PageableInfo result = PageCalculator.toPageableInfo(1, 10);

        assertThat(result.getOffset()).isEqualTo(0);
        assertThat(result.getLimit()).isEqualTo(10);
    }

    @Test
    @DisplayName("toPageableInfo - 두 번째 페이지 offset 계산")
    void toPageableInfo_두번째_페이지() {
        PageableInfo result = PageCalculator.toPageableInfo(2, 10);

        assertThat(result.getOffset()).isEqualTo(10);
        assertThat(result.getLimit()).isEqualTo(10);
    }

    @Test
    @DisplayName("toDefaultPageableInfo - 기본값 offset=0, limit=1")
    void toDefaultPageableInfo() {
        PageableInfo result = PageCalculator.toDefaultPageableInfo();

        assertThat(result.getOffset()).isEqualTo(0);
        assertThat(result.getLimit()).isEqualTo(1);
    }

    @Test
    @DisplayName("toPageInfo - offset/limit 기반 PageInfo 생성")
    void toPageInfo_offset_limit() {
        String content = "test";

        PageInfo<String> result = PageCalculator.toPageInfo(content, 0, 10, 30L, 5);

        assertThat(result.getContent()).isEqualTo("test");
        assertThat(result.getPageNum()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalCount()).isEqualTo(30L);
        assertThat(result.getTotalPage()).isEqualTo(3);
        assertThat(result.getElementSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("toPageInfo - PageableInfo 기반 PageInfo 생성")
    void toPageInfo_pageableInfo() {
        PageableInfo pageableInfo = PageableInfo.of(10, 10);
        String content = "test";

        PageInfo<String> result = PageCalculator.toPageInfo(content, pageableInfo, 30L, 5);

        assertThat(result.getContent()).isEqualTo("test");
        assertThat(result.getPageNum()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalCount()).isEqualTo(30L);
        assertThat(result.getTotalPage()).isEqualTo(3);
        assertThat(result.getElementSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("toPageInfo - totalCount가 limit으로 나누어 떨어지지 않을 때 totalPage 올림 계산")
    void toPageInfo_totalPage_올림_계산() {
        PageInfo<String> result = PageCalculator.toPageInfo("test", 0, 10, 25L, 5);

        assertThat(result.getTotalPage()).isEqualTo(3);
    }
}
