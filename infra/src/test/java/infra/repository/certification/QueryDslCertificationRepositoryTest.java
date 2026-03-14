package infra.repository.certification;

import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslCertificationRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslCertificationRepository queryDslCertificationRepository;

    @Test
    @DisplayName("findCertificationByEmail - 이메일로 인증코드 조회")
    void findCertificationByEmail_인증코드_조회() {
        String result = queryDslCertificationRepository.findCertificationByEmail("user1@test.com");

        assertThat(result).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("findCertificationByEmail - 존재하지 않는 이메일은 null 반환")
    void findCertificationByEmail_존재하지_않는_이메일() {
        String result = queryDslCertificationRepository.findCertificationByEmail("unknown@test.com");

        assertThat(result).isNull();
    }
}
