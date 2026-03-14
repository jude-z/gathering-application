package infra.repository.gathering;

import entity.gathering.Gathering;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.gathering.GatheringsProjection;
import infra.repository.dto.querydsl.gathering.ParticipatedProjection;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslGatheringRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslGatheringRepository queryDslGatheringRepository;

    @Test
    @DisplayName("gatheringParticipated - 모임 참가자 조회")
    void gatheringParticipated_참가자_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<ParticipatedProjection> result =
                queryDslGatheringRepository.gatheringParticipated(pageableInfo, gathering1.getId());

        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("gatheringsCategory - 카테고리별 모임 조회")
    void gatheringsCategory_카테고리별_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<GatheringsProjection> result =
                queryDslGatheringRepository.gatheringsCategory(pageableInfo, "category1");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Gathering 1");
    }

    @Test
    @DisplayName("gatheringsLike - 좋아요한 모임 조회")
    void gatheringsLike_좋아요_모임_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<GatheringsProjection> result =
                queryDslGatheringRepository.gatheringsLike(pageableInfo, user1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Gathering 1");
    }

    @Test
    @DisplayName("gatheringsRecommend - 추천 모임 조회")
    void gatheringsRecommend_추천_모임_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<GatheringsProjection> result =
                queryDslGatheringRepository.gatheringsRecommend(pageableInfo, LocalDate.of(2025, 3, 1));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Gathering 1");
    }

    @Test
    @DisplayName("findGatheringFetchCreatedBy - 작성자와 함께 모임 조회")
    void findGatheringFetchCreatedBy_작성자_함께_조회() {
        Optional<Gathering> result = queryDslGatheringRepository.findGatheringFetchCreatedBy(gathering1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Gathering 1");
        assertThat(result.get().getCreateBy().getUsername()).isEqualTo("user1");
    }
}
