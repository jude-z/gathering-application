package querydsl.repository.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.QueryDslCategoryRepository;
import jakarta.persistence.EntityManager;
import infra.repository.category.CategoryRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslCategoryRepositoryTest {

    QueryDslCategoryRepository queryDslCategoryRepository;

    @Autowired EntityManager em;
    @Autowired CategoryRepository categoryRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;

    Image image;
    User user;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        queryDslCategoryRepository = new QueryDslCategoryRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        user = returnDummyUser(1, image);
        category = returnDummyCategory(1);

        imageRepository.save(image);
        userRepository.save(user);
        categoryRepository.save(category);

        gathering = returnDummyGathering(1, category, user, image);
        gatheringRepository.save(gathering);
    }

    @Test
    void findBy() {
        Optional<Category> result = queryDslCategoryRepository.findBy(gathering.getId(), "category1");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("category1");
    }

    @Test
    void findBy_notFound() {
        Optional<Category> result = queryDslCategoryRepository.findBy(gathering.getId(), "nonExistent");

        assertThat(result).isEmpty();
    }
}
