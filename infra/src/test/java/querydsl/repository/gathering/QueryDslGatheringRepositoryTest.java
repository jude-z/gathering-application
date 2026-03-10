package querydsl.repository.gathering;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import infra.dto.querydsl.gathering.GatheringsProjection;
import infra.dto.querydsl.gathering.ParticipatedProjection;
import entity.category.Category;
import entity.fcm.FCMToken;
import entity.fcm.Topic;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.like.Like;
import entity.recommend.Recommend;
import entity.user.User;
import infra.repository.gathering.QueryDslGatheringRepository;
import jakarta.persistence.EntityManager;
import infra.repository.category.CategoryRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.fcm.FCMTokenRepository;
import infra.repository.fcm.TopicRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.like.LikeRepository;
import infra.repository.recommend.RecommendRepository;
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
class QueryDslGatheringRepositoryTest {

    QueryDslGatheringRepository queryDslGatheringRepository;

    @Autowired EntityManager em;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired LikeRepository likeRepository;
    @Autowired RecommendRepository recommendRepository;
    @Autowired TopicRepository topicRepository;
    @Autowired FCMTokenRepository fcmTokenRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        queryDslGatheringRepository = new QueryDslGatheringRepository(new JPAQueryFactory(em));
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
    void gatheringParticipated() {
        enrollmentRepository.save(returnDummyEnrollment(users.get(1), gathering));
        enrollmentRepository.save(returnDummyEnrollment(users.get(2), gathering));

        QueryDslPageResponse<ParticipatedProjection> result = queryDslGatheringRepository
                .gatheringParticipated(PageableInfo.of(0, 10), gathering.getId());

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void gatheringsCategory() {
        QueryDslPageResponse<GatheringsProjection> result = queryDslGatheringRepository
                .gatheringsCategory(PageableInfo.of(0, 10), "category1");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("category1");
    }

    @Test
    void gatheringsCategory_notFound() {
        QueryDslPageResponse<GatheringsProjection> result = queryDslGatheringRepository
                .gatheringsCategory(PageableInfo.of(0, 10), "nonExistent");

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void gatheringsLike() {
        Like like = returnDummyLike(users.get(1), gathering);
        likeRepository.save(like);

        QueryDslPageResponse<GatheringsProjection> result = queryDslGatheringRepository
                .gatheringsLike(PageableInfo.of(0, 10), users.get(1).getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void gatheringsLike_notFound() {
        QueryDslPageResponse<GatheringsProjection> result = queryDslGatheringRepository
                .gatheringsLike(PageableInfo.of(0, 10), users.get(1).getId());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void gatheringsRecommend() {
        Recommend recommend = returnDummyRecommend(gathering, 10);
        recommendRepository.save(recommend);

        QueryDslPageResponse<GatheringsProjection> result = queryDslGatheringRepository
                .gatheringsRecommend(PageableInfo.of(0, 10), LocalDate.now());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void gatheringsRecommend_empty() {
        QueryDslPageResponse<GatheringsProjection> result = queryDslGatheringRepository
                .gatheringsRecommend(PageableInfo.of(0, 10), LocalDate.now());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findTopicById() {
        Topic topic = returnDummyTopic("testTopic", gathering);
        topicRepository.save(topic);

        Optional<Gathering> result = queryDslGatheringRepository.findTopicById(gathering.getId());

        assertThat(result).isPresent();
    }

    @Test
    void findGatheringFetchCreatedByAndTokensId() {
        FCMToken token = returnDummyFCMToken(users.get(0), 1);
        fcmTokenRepository.save(token);

        Optional<Gathering> result = queryDslGatheringRepository
                .findGatheringFetchCreatedByAndTokensId(gathering.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCreateBy().getUsername()).isEqualTo("user1");
    }

    @Test
    void findGatheringFetchCreatedAndTopicBy() {
        Topic topic = returnDummyTopic("testTopic", gathering);
        topicRepository.save(topic);

        Optional<Gathering> result = queryDslGatheringRepository
                .findGatheringFetchCreatedAndTopicBy(gathering.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCreateBy().getUsername()).isEqualTo("user1");
    }

    @Test
    void findGatheringFetchCreatedByAndTokensId_notFound() {
        Optional<Gathering> result = queryDslGatheringRepository
                .findGatheringFetchCreatedByAndTokensId(999L);

        assertThat(result).isEmpty();
    }
}
