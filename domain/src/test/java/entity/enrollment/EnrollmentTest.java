package entity.enrollment;

import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EnrollmentTest {

    @Test
    @DisplayName("Builder로 Enrollment 정상 생성")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        Enrollment enrollment = Enrollment.builder()
                .accepted(false)
                .enrolledBy(user)
                .date(now)
                .build();

        assertThat(enrollment.isAccepted()).isFalse();
        assertThat(enrollment.getEnrolledBy()).isEqualTo(user);
        assertThat(enrollment.getDate()).isEqualTo(now);
    }

    @Test
    @DisplayName("changeAccepted() 호출 시 accepted가 true로 변경")
    void changeAccepted_승인_처리() {
        Enrollment enrollment = Enrollment.builder()
                .accepted(false)
                .date(LocalDateTime.now())
                .build();

        enrollment.changeAccepted();

        assertThat(enrollment.isAccepted()).isTrue();
    }
}
