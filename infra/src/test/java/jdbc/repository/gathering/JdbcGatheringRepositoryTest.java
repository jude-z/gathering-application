package jdbc.repository.gathering;

import config.TestConfig;
import infra.dto.jdbc.gathering.GatheringDetailProjection;
import infra.dto.jdbc.gathering.MainGatheringsProjection;
import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.gathering.JdbcGatheringRepository;
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
@Import(JdbcGatheringRepository.class)
class JdbcGatheringRepositoryTest {

    @Autowired JdbcGatheringRepository jdbcGatheringRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired EnrollmentRepository enrollmentRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, users.get(0), image);
    }

    private void saveBaseData() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.save(gathering);
    }

    @Test
    void gatheringDetail() {
        saveBaseData();
        Enrollment enrollment = returnDummyEnrollment(users.get(1), gathering);
        enrollmentRepository.save(enrollment);

        List<GatheringDetailProjection> result = jdbcGatheringRepository.gatheringDetail(gathering.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTitle()).isEqualTo("title1");
        assertThat(result.get(0).getCategory()).isEqualTo("category1");
    }

    @Test
    void gatherings() {
        saveBaseData();

        List<MainGatheringsProjection> result = jdbcGatheringRepository.gatherings();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTitle()).isEqualTo("title1");
    }

    @Test
    void subGatherings() {
        saveBaseData();

        List<MainGatheringsProjection> result = jdbcGatheringRepository.subGatherings("category1");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategory()).isEqualTo("category1");
    }

    @Test
    void subGatherings_notFound() {
        saveBaseData();

        List<MainGatheringsProjection> result = jdbcGatheringRepository.subGatherings("nonExistent");

        assertThat(result).isEmpty();
    }

    @Test
    void updateCount() {
        saveBaseData();

        jdbcGatheringRepository.updateCount(gathering.getId(), 5);

        Gathering updated = gatheringRepository.findById(gathering.getId()).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(6);
    }
}
