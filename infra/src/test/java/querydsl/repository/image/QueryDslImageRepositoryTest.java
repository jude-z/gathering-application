package querydsl.repository.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.image.QueryDslImageRepository;
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

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslImageRepositoryTest {

    QueryDslImageRepository queryDslImageRepository;

    @Autowired EntityManager em;
    @Autowired ImageRepository imageRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired CategoryRepository categoryRepository;

    Image image;
    User user;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        queryDslImageRepository = new QueryDslImageRepository(new JPAQueryFactory(em));
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
    void gatheringImage() {
        Image gatheringImg1 = Image.builder().url("gImg1").gathering(gathering).build();
        Image gatheringImg2 = Image.builder().url("gImg2").gathering(gathering).build();
        imageRepository.save(gatheringImg1);
        imageRepository.save(gatheringImg2);

        QueryDslPageResponse<String> result = queryDslImageRepository
                .gatheringImage(gathering.getId(), PageableInfo.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactlyInAnyOrder("gImg1", "gImg2");
    }

    @Test
    void gatheringImage_empty() {
        QueryDslPageResponse<String> result = queryDslImageRepository
                .gatheringImage(gathering.getId(), PageableInfo.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }
}
