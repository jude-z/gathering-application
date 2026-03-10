package querydsl.repository.enrollment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.fcm.FCMToken;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.enrollment.QueryDslEnrollmentRepository;
import jakarta.persistence.EntityManager;
import infra.repository.category.CategoryRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.fcm.FCMTokenRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslEnrollmentRepositoryTest {

    QueryDslEnrollmentRepository queryDslEnrollmentRepository;

    @Autowired EntityManager em;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired FCMTokenRepository fcmTokenRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        queryDslEnrollmentRepository = new QueryDslEnrollmentRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);

        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);

        gathering = returnDummyGathering(1, category, users.get(0), image);
        gatheringRepository.save(gathering);
    }

    @Test
    void existEnrollment() {
        Enrollment enrollment = returnDummyEnrollment(users.get(0), gathering);
        enrollmentRepository.save(enrollment);

        Enrollment result = queryDslEnrollmentRepository.existEnrollment(gathering.getId(), users.get(0).getId());

        assertThat(result).isNotNull();
    }

    @Test
    void existEnrollment_notFound() {
        Enrollment result = queryDslEnrollmentRepository.existEnrollment(gathering.getId(), users.get(1).getId());

        assertThat(result).isNull();
    }

    @Test
    void findEnrollment() {
        FCMToken token = returnDummyFCMToken(users.get(0), 1);
        fcmTokenRepository.save(token);

        Enrollment enrollment = returnDummyEnrollment(users.get(0), gathering);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> result = queryDslEnrollmentRepository
                .findEnrollment(gathering.getId(), users.get(0).getId(), true);

        assertThat(result).isPresent();
        assertThat(result.get().getEnrolledBy().getUsername()).isEqualTo("user1");
    }

    @Test
    void findEnrollment_notAccepted() {
        Enrollment enrollment = returnDummyEnrollment(users.get(0), gathering);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> result = queryDslEnrollmentRepository
                .findEnrollment(gathering.getId(), users.get(0).getId(), false);

        assertThat(result).isEmpty();
    }

    @Test
    void findEnrollmentEnrolledByAndTokensById() {
        Enrollment enrollment = Enrollment.builder()
                .date(java.time.LocalDateTime.now())
                .accepted(false)
                .gathering(gathering)
                .enrolledBy(users.get(0))
                .build();
        enrollmentRepository.save(enrollment);

        FCMToken token = returnDummyFCMToken(users.get(0), 1);
        fcmTokenRepository.save(token);

        Optional<Enrollment> result = queryDslEnrollmentRepository
                .findEnrollmentEnrolledByAndTokensById(enrollment.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getEnrolledBy().getUsername()).isEqualTo("user1");
    }

    @Test
    void findEnrollmentEnrolledByAndTokensById_accepted() {
        Enrollment enrollment = returnDummyEnrollment(users.get(0), gathering);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> result = queryDslEnrollmentRepository
                .findEnrollmentEnrolledByAndTokensById(enrollment.getId());

        assertThat(result).isEmpty();
    }
}
