package querydsl.repository.fcm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.category.Category;
import entity.fcm.FCMToken;
import entity.fcm.Topic;
import entity.fcm.UserTopic;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.fcm.QueryDslFcmRepository;
import jakarta.persistence.EntityManager;
import infra.repository.category.CategoryRepository;
import infra.repository.fcm.FCMTokenRepository;
import infra.repository.fcm.TopicRepository;
import infra.repository.fcm.UserTopicRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslFcmRepositoryTest {

    QueryDslFcmRepository queryDslFcmRepository;

    @Autowired EntityManager em;
    @Autowired FCMTokenRepository fcmTokenRepository;
    @Autowired TopicRepository topicRepository;
    @Autowired UserTopicRepository userTopicRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired GatheringRepository gatheringRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;
    Topic topic;
    List<FCMToken> tokens;

    @BeforeEach
    void beforeEach() {
        queryDslFcmRepository = new QueryDslFcmRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);

        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);

        gathering = returnDummyGathering(1, category, users.get(0), image);
        gatheringRepository.save(gathering);

        topic = returnDummyTopic("testTopic", gathering);
        topicRepository.save(topic);

        tokens = List.of(
                returnDummyFCMToken(users.get(0), 1),
                returnDummyFCMToken(users.get(1), 2));
        fcmTokenRepository.saveAll(tokens);
    }

    @Test
    void findByTokenValueAndUser() {
        Optional<FCMToken> result = queryDslFcmRepository
                .findByTokenValueAndUser("token1", users.get(0).getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTokenValue()).isEqualTo("token1");
    }

    @Test
    void findByTokenValueAndUser_notFound() {
        Optional<FCMToken> result = queryDslFcmRepository
                .findByTokenValueAndUser("token1", users.get(1).getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByExpirationDate() {
        QueryDslPageResponse<FCMToken> result = queryDslFcmRepository
                .findByExpirationDate(PageableInfo.of(0, 10), LocalDate.now());

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findByUserId() {
        UserTopic userTopic = returnDummyUserTopic(topic, users.get(0));
        userTopicRepository.save(userTopic);

        QueryDslPageResponse<UserTopic> result = queryDslFcmRepository
                .findByUserId(PageableInfo.of(0, 10), users.get(0).getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void existsByTopicAndUser() {
        UserTopic userTopic = returnDummyUserTopic(topic, users.get(0));
        userTopicRepository.save(userTopic);

        boolean exists = queryDslFcmRepository.existsByTopicAndUser("testTopic", users.get(0).getId());
        boolean notExists = queryDslFcmRepository.existsByTopicAndUser("testTopic", users.get(1).getId());

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
