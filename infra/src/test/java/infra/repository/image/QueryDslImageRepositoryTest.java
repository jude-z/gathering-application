package infra.repository.image;

import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslImageRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslImageRepository queryDslImageRepository;

    @Test
    @DisplayName("gatheringImage - 모임 갤러리 이미지 URL 조회")
    void gatheringImage_이미지_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<String> result =
                queryDslImageRepository.gatheringImage(gathering1.getId(), pageableInfo);

        assertThat(result.getContent()).contains("gallery1.jpg");
        assertThat(result.getTotalCount()).isGreaterThanOrEqualTo(1);
    }
}
