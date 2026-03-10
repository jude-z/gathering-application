package querydsl.repository.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.like.Like;
import entity.user.User;
import infra.repository.like.QueryDslLikeRepository;
import jakarta.persistence.EntityManager;
import infra.repository.category.CategoryRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.like.LikeRepository;
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
class QueryDslLikeRepositoryTest {

    QueryDslLikeRepository queryDslLikeRepository;

    @Autowired EntityManager em;
    @Autowired LikeRepository likeRepository;
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
        queryDslLikeRepository = new QueryDslLikeRepository(new JPAQueryFactory(em));
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
    void findLike() {
        Like like = returnDummyLike(user, gathering);
        likeRepository.save(like);

        Optional<Like> result = queryDslLikeRepository.findLike(user.getId(), gathering.getId());

        assertThat(result).isPresent();
    }

    @Test
    void findLike_notFound() {
        Optional<Like> result = queryDslLikeRepository.findLike(user.getId(), gathering.getId());

        assertThat(result).isEmpty();
    }
}
