package jpa.repository.user;

import config.TestConfig;
import entity.image.Image;
import entity.user.User;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired ImageRepository imageRepository;

    Image image;
    List<User> users;

    @BeforeEach
    void beforeEach() {
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
    }

    @Test
    void existsByUsername() {
        imageRepository.save(image);
        userRepository.saveAll(users);

        assertThat(userRepository.existsByUsername("user1")).isTrue();
        assertThat(userRepository.existsByUsername("user99")).isFalse();
    }

    @Test
    void existsByNickname() {
        imageRepository.save(image);
        userRepository.saveAll(users);

        assertThat(userRepository.existsByNickname("nickname1")).isTrue();
        assertThat(userRepository.existsByNickname("nickname99")).isFalse();
    }
}
