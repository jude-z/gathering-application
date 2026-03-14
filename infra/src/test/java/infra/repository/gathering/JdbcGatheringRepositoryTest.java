package infra.repository.gathering;

import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcGatheringRepositoryTest extends RepositoryTestSupport {

    @Autowired
    JdbcGatheringRepository jdbcGatheringRepository;

    @Test
    @DisplayName("updateCount - 모임 카운트 증가")
    void updateCount_카운트_증가() {
        jdbcGatheringRepository.updateCount(gathering1.getId(), 1);

        em.clear();
        int updatedCount = em.find(entity.gathering.Gathering.class, gathering1.getId()).getCount();
        assertThat(updatedCount).isEqualTo(11);
    }

    @Test
    @DisplayName("updateCount - 모임 카운트 감소")
    void updateCount_카운트_감소() {
        jdbcGatheringRepository.updateCount(gathering1.getId(), -1);

        em.clear();
        int updatedCount = em.find(entity.gathering.Gathering.class, gathering1.getId()).getCount();
        assertThat(updatedCount).isEqualTo(9);
    }
}
