package infra.repository.enrollment;

import entity.enrollment.Enrollment;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslEnrollmentRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslEnrollmentRepository queryDslEnrollmentRepository;

    @Test
    @DisplayName("existEnrollment - 등록이 존재하면 Enrollment 반환")
    void existEnrollment_존재하는_등록() {
        Enrollment result = queryDslEnrollmentRepository.existEnrollment(gathering1.getId(), user2.getId());

        assertThat(result).isNotNull();
        assertThat(result.getEnrolledBy().getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("existEnrollment - 등록이 없으면 null 반환")
    void existEnrollment_존재하지_않는_등록() {
        Enrollment result = queryDslEnrollmentRepository.existEnrollment(gathering2.getId(), user1.getId());

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findEnrollment - 승인된 등록 조회")
    void findEnrollment_승인된_등록_조회() {
        Optional<Enrollment> result = queryDslEnrollmentRepository.findEnrollment(
                gathering1.getId(), user2.getId(), true);

        assertThat(result).isPresent();
        assertThat(result.get().isAccepted()).isTrue();
    }

    @Test
    @DisplayName("findEnrollmentEnrolledByAndTokensById - 미승인 등록 조회")
    void findEnrollmentEnrolledByAndTokensById_미승인_등록_조회() {
        Optional<Enrollment> result = queryDslEnrollmentRepository
                .findEnrollmentEnrolledByAndTokensById(enrollment2.getId());

        assertThat(result).isPresent();
        assertThat(result.get().isAccepted()).isFalse();
        assertThat(result.get().getEnrolledBy().getUsername()).isEqualTo("user1");
    }
}
