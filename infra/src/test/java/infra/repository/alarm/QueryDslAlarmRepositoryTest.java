package infra.repository.alarm;

import entity.alarm.Alarm;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslAlarmRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslAlarmRepository queryDslAlarmRepository;

    @Test
    @DisplayName("findUncheckedAlarm - 미확인 알람만 조회")
    void findUncheckedAlarm_미확인_알람_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<Alarm> result = queryDslAlarmRepository.findUncheckedAlarm(pageableInfo, user1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Unchecked Alarm");
        assertThat(result.getTotalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("findCheckedAlarm - 확인된 알람만 조회")
    void findCheckedAlarm_확인_알람_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<Alarm> result = queryDslAlarmRepository.findCheckedAlarm(pageableInfo, user1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Checked Alarm");
        assertThat(result.getTotalCount()).isEqualTo(1);
    }
}
