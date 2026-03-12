package infra.repository.gathering;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.like.Like;
import entity.recommend.Recommend;
import entity.user.User;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.gathering.GatheringsProjection;
import infra.repository.dto.querydsl.gathering.ParticipatedProjection;
import infra.repository.category.CategoryRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.image.ImageRepository;
import infra.repository.like.LikeRepository;
import infra.repository.recommend.RecommendRepository;
import infra.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import util.page.PageableInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static infra.utils.DummyData.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.yml")
class QueryDslGatheringRepositoryTest {

    @Autowired EntityManager em;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired UserRepository userRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired LikeRepository likeRepository;
    @Autowired RecommendRepository recommendRepository;

    QueryDslGatheringRepository queryDslGatheringRepository;

    Category category;
    Image userImage, gatheringImage;
    List<User> users;
    List<Gathering> gatherings;
    List<Enrollment> enrollments;
    List<Like> likes;

    @BeforeEach
    void setUp() {
        queryDslGatheringRepository = new QueryDslGatheringRepository(new JPAQueryFactory(em));
        category = returnDummyCategory(1);
        userImage = returnDummyImage(1);
        gatheringImage = returnDummyImage(2);
        users = List.of(returnDummyUser(1, userImage), returnDummyUser(2, userImage), returnDummyUser(3, userImage));
        User user1 = users.get(0);
        gatherings = List.of(
                returnDummyGathering(1, category, user1, gatheringImage),
                returnDummyGathering(2, category, user1, gatheringImage),
                returnDummyGathering(3, category, user1, gatheringImage),
                returnDummyGathering(4, category, user1, gatheringImage),
                returnDummyGathering(5, category, user1, gatheringImage));
        Gathering g1 = gatherings.get(0);
        enrollments = List.of(
                returnDummyEnrollment(users.get(0), g1),
                returnDummyEnrollment(users.get(1), g1),
                returnDummyEnrollment(users.get(2), g1));
        likes = List.of(returnDummyLike(users.get(1), g1), returnDummyLike(users.get(2), g1));
    }

    private void saveAll() {
        imageRepository.saveAll(List.of(userImage, gatheringImage));
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.saveAll(gatherings);
        enrollmentRepository.saveAll(enrollments);
    }

    @Test
    void gatheringParticipated() {
        saveAll();

        PageableInfo pageableInfo = PageableInfo.of(0, 10);
        QueryDslPageResponse<ParticipatedProjection> response =
                queryDslGatheringRepository.gatheringParticipated(pageableInfo, gatherings.get(0).getId());

        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getContent()).extracting("participatedBy")
                .containsExactly("user1", "user2", "user3");
    }

    @Test
    void gatheringsCategory() {
        saveAll();

        PageableInfo pageableInfo = PageableInfo.of(0, 2);
        QueryDslPageResponse<GatheringsProjection> response =
                queryDslGatheringRepository.gatheringsCategory(pageableInfo, category.getName());

        assertThat(response.getTotalCount()).isEqualTo(5);
        assertThat(response.getTotalPage()).isEqualTo(3);
    }

    @Test
    void gatheringsLike() {
        saveAll();
        likeRepository.saveAll(likes);

        PageableInfo pageableInfo = PageableInfo.of(0, 10);
        QueryDslPageResponse<GatheringsProjection> response =
                queryDslGatheringRepository.gatheringsLike(pageableInfo, users.get(1).getId());

        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void gatheringsRecommend() {
        saveAll();
        Recommend recommend = returnDummyRecommend(gatherings.get(0));
        recommendRepository.save(recommend);

        PageableInfo pageableInfo = PageableInfo.of(0, 10);
        QueryDslPageResponse<GatheringsProjection> response =
                queryDslGatheringRepository.gatheringsRecommend(pageableInfo, LocalDate.now());

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void findGatheringFetchCreatedBy() {
        saveAll();

        Optional<Gathering> result = queryDslGatheringRepository.findGatheringFetchCreatedBy(gatherings.get(0).getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCreateBy()).isEqualTo(users.get(0));
    }
}
