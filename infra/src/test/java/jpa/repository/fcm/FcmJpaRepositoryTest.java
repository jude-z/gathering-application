package jpa.repository.fcm;

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
import infra.repository.fcm.FCMTokenRepository;
import infra.repository.fcm.FCMTokenTopicRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class FcmJpaRepositoryTest {

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserTopicRepository userTopicRepository;
    @Autowired
    FCMTokenRepository fcmTokenRepository;
    @Autowired
    FCMTokenTopicRepository fcmTokenTopicRepository;
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
    List<FCMTokenTopic> tokenTopics;
    List<UserTopic> userTopics;

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
    void findByTopicName() {
        saveBaseData();

        Optional<Topic> result = topicRepository.findByTopicName("testTopic");

        assertThat(result).isPresent();
        assertThat(result.get().getTopicName()).isEqualTo("testTopic");
    }

    @Test
    void findByTopicName_notFound() {
        saveBaseData();

        Optional<Topic> result = topicRepository.findByTopicName("nonExistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUser() {
        saveBaseData();
        userTopics = List.of(
                returnDummyUserTopic(topic, users.get(0)),
                returnDummyUserTopic(topic, users.get(1)));
        userTopicRepository.saveAll(userTopics);

        List<UserTopic> result = userTopicRepository.findByUser(users.get(0));

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFcmTokenIn() {
        saveBaseData();
        tokenTopics = List.of(
                returnDummyFCMTokenTopic(topic, tokens.get(0)),
                returnDummyFCMTokenTopic(topic, tokens.get(1)));
        fcmTokenTopicRepository.saveAll(tokenTopics);

        List<FCMTokenTopic> result = fcmTokenTopicRepository.findByFcmTokenIn(List.of(tokens.get(0), tokens.get(1)));

        assertThat(result).hasSize(2);
    }

    @Test
    void findByFcmTokenIn_empty() {
        saveBaseData();

        List<FCMTokenTopic> result = fcmTokenTopicRepository.findByFcmTokenIn(List.of(tokens.get(0)));

        assertThat(result).isEmpty();
    }
}
