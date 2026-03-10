package jdbc.repository.recommend;

import config.TestConfig;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.recommend.JdbcRecommendRepository;
import infra.repository.recommend.RecommendRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
@Import(JdbcRecommendRepository.class)
class JdbcRecommendRepositoryTest {

    @Autowired JdbcRecommendRepository jdbcRecommendRepository;
    @Autowired RecommendRepository recommendRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;

    Image image;
    User user;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        image = returnDummyImage(1);
        user = returnDummyUser(1, image);
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, user, image);
    }

    private void saveBaseData() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.save(user);
        gatheringRepository.save(gathering);
    }

    @Test
    void updateCount() {
        saveBaseData();

        int result = jdbcRecommendRepository.updateCount(gathering.getId(), LocalDate.now(), 1);

        assertThat(result).isGreaterThan(0);
    }
}
