package infra.repository.meeting;

import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcMeetingRepositoryTest extends RepositoryTestSupport {

    @Autowired
    JdbcMeetingRepository jdbcMeetingRepository;

    @Test
    @DisplayName("updateCount - 미팅 카운트 증가")
    void updateCount_카운트_증가() {
        jdbcMeetingRepository.updateCount(meeting1.getId(), 1);

        em.clear();
        int updatedCount = em.find(entity.meeting.Meeting.class, meeting1.getId()).getCount();
        assertThat(updatedCount).isEqualTo(4);
    }
}
