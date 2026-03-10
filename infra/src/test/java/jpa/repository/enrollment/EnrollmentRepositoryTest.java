package jpa.repository.enrollment;

import config.TestConfig;
import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.enrollment.EnrollmentRepository;
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
class EnrollmentRepositoryTest {

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired UserRepository userRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ImageRepository imageRepository;

    Category category;
    Image image;
    List<User> users;
    Gathering gathering;
    List<Enrollment> enrollments;

    @BeforeEach
    void beforeEach() {
        image = returnDummyImage(1);
        category = returnDummyCategory(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        gathering = returnDummyGathering(1, category, users.get(0), image);
        enrollments = List.of(
                returnDummyEnrollment(users.get(0), gathering),
                returnDummyEnrollment(users.get(1), gathering));
    }

    @Test
    void findByGatheringAndEnrolledBy() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.save(gathering);
        enrollmentRepository.saveAll(enrollments);

        Optional<Enrollment> result = enrollmentRepository.findByGatheringAndEnrolledBy(gathering, users.get(0));

        assertThat(result).isPresent();
        assertThat(result.get().getEnrolledBy()).isEqualTo(users.get(0));
    }

    @Test
    void findByGatheringAndEnrolledBy_notFound() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.save(gathering);

        Optional<Enrollment> result = enrollmentRepository.findByGatheringAndEnrolledBy(gathering, users.get(0));

        assertThat(result).isEmpty();
    }
}
