package infra.repository.meeting;

import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.meeting.MeetingProjection;
import infra.repository.dto.querydsl.meeting.MeetingsProjection;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslMeetingRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslMeetingRepository queryDslMeetingRepository;

    @Test
    @DisplayName("meetingDetail - 미팅 상세 조회")
    void meetingDetail_상세_조회() {
        QueryDslPageResponse<MeetingProjection> result =
                queryDslMeetingRepository.meetingDetail(meeting1.getId());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Meeting 1");
        assertThat(result.getContent().get(0).getCreatedBy()).isEqualTo("user1");
    }

    @Test
    @DisplayName("meetings - 모임별 미팅 목록 조회")
    void meetings_목록_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<MeetingsProjection> result =
                queryDslMeetingRepository.meetings(pageableInfo, gathering1.getId());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Meeting 1");
        assertThat(result.getTotalCount()).isEqualTo(1);
    }
}
