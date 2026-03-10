package jdbc.repository.fcm;

import config.TestConfig;
import entity.category.Category;
import entity.fcm.FCMToken;
import entity.fcm.FCMTokenTopic;
import entity.fcm.Topic;
import entity.fcm.UserTopic;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.fcm.*;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
@Import(JdbcFcmRepository.class)
class JdbcFcmRepositoryTest {

    @Autowired JdbcFcmRepository jdbcFcmRepository;
    @Autowired FCMTokenRepository fcmTokenRepository;
    @Autowired FCMTokenTopicRepository fcmTokenTopicRepository;
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
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, users.get(0), image);
        topic = returnDummyTopic("testTopic", gathering);
        tokens = List.of(
                returnDummyFCMToken(users.get(0), 1),
                returnDummyFCMToken(users.get(0), 2),
                returnDummyFCMToken(users.get(1), 3));
    }

    private void saveBaseData() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.save(gathering);
        topicRepository.save(topic);
        fcmTokenRepository.saveAll(tokens);
    }

    @Test
    void deleteTokenByTokenValueIn() {
        saveBaseData();

        jdbcFcmRepository.deleteTokenByTokenValueIn(List.of("token1", "token2"));

        List<FCMToken> remaining = fcmTokenRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getTokenValue()).isEqualTo("token3");
    }

    @Test
    void deleteTokenByTokenValueIn_emptyList() {
        saveBaseData();

        jdbcFcmRepository.deleteTokenByTokenValueIn(List.of());

        assertThat(fcmTokenRepository.findAll()).hasSize(3);
    }

    @Test
    void deleteTokenTopicByTokenValueIn() {
        saveBaseData();
        List<FCMTokenTopic> tokenTopics = List.of(
                returnDummyFCMTokenTopic(topic, tokens.get(0)),
                returnDummyFCMTokenTopic(topic, tokens.get(1)));
        fcmTokenTopicRepository.saveAll(tokenTopics);

        jdbcFcmRepository.deleteTokenTopicByTokenValueIn(List.of("token1"));

        List<FCMTokenTopic> remaining = fcmTokenTopicRepository.findAll();
        assertThat(remaining).hasSize(1);
    }

    @Test
    void deleteUserTopicByTopicAndUser() {
        saveBaseData();
        UserTopic userTopic = returnDummyUserTopic(topic, users.get(0));
        userTopicRepository.save(userTopic);

        jdbcFcmRepository.deleteUserTopicByTopicAndUser(topic, users.get(0));

        List<UserTopic> remaining = userTopicRepository.findAll();
        assertThat(remaining).isEmpty();
    }
}
