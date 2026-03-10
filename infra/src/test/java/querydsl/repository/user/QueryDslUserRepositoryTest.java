package querydsl.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.fcm.FCMToken;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.user.QueryDslUserRepository;
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
class QueryDslUserRepositoryTest {

    QueryDslUserRepository queryDslUserRepository;

    @Autowired EntityManager em;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired FCMTokenRepository fcmTokenRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        queryDslUserRepository = new QueryDslUserRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image), returnDummyUser(3, image));
        category = returnDummyCategory(1);

        imageRepository.save(image);
        userRepository.saveAll(users);
        categoryRepository.save(category);

        gathering = returnDummyGathering(1, category, users.get(0), image);
        gatheringRepository.save(gathering);
    }

    @Test
    void findByEmail() {
        QueryDslPageResponse<User> result = queryDslUserRepository
                .findByEmail(PageableInfo.of(0, 10), "email1");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("email1");
    }

    @Test
    void findByEmail_notFound() {
        QueryDslPageResponse<User> result = queryDslUserRepository
                .findByEmail(PageableInfo.of(0, 10), "nonExistent");

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByIdFetchImage() {
        Optional<User> result = queryDslUserRepository.findByIdFetchImage(users.get(0).getId());

        assertThat(result).isPresent();
        assertThat(result.get().getProfileImage()).isNotNull();
        assertThat(result.get().getProfileImage().getUrl()).isEqualTo("image1Url");
    }

    @Test
    void findByIdFetchImage_notFound() {
        Optional<User> result = queryDslUserRepository.findByIdFetchImage(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAndTokenByUserId() {
        FCMToken token = returnDummyFCMToken(users.get(0), 1);
        fcmTokenRepository.save(token);

        Optional<User> result = queryDslUserRepository.findAndTokenByUserId(users.get(0).getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTokens()).hasSize(1);
    }

    @Test
    void findByUsername() {
        Optional<User> result = queryDslUserRepository.findByUsername("user1");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
    }

    @Test
    void findByUsername_notFound() {
        Optional<User> result = queryDslUserRepository.findByUsername("nonExistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findEnrollmentById() {
        Enrollment e1 = returnDummyEnrollment(users.get(0), gathering);
        Enrollment e2 = returnDummyEnrollment(users.get(1), gathering);
        Enrollment e3 = returnDummyEnrollment(users.get(2), gathering);
        enrollmentRepository.saveAll(List.of(e1, e2, e3));

        QueryDslPageResponse<User> result = queryDslUserRepository
                .findEnrollmentById(PageableInfo.of(0, 10), gathering.getId(), users.get(0).getId());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("username")
                .containsExactlyInAnyOrder("user2", "user3");
    }
}
