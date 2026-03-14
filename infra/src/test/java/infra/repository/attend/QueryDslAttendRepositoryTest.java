package infra.repository.attend;

import entity.attend.Attend;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslAttendRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslAttendRepository queryDslAttendRepository;

    @Test
    @DisplayName("findByUserIdAndMeetingId - 유저의 미팅 참석 조회")
    void findByUserIdAndMeetingId_참석_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<Attend> result =
                queryDslAttendRepository.findByUserIdAndMeetingId(pageableInfo, user2.getId(), meeting1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByUserIdAndMeetingId - 참석하지 않은 경우 빈 결과")
    void findByUserIdAndMeetingId_참석하지_않은_경우() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<Attend> result =
                queryDslAttendRepository.findByUserIdAndMeetingId(pageableInfo, user1.getId(), meeting1.getId());

        assertThat(result.getContent()).isEmpty();
    }
}
