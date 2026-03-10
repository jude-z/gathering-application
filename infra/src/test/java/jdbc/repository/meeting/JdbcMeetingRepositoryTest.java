package jdbc.repository.meeting;

import config.TestConfig;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.meeting.Meeting;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.meeting.JdbcMeetingRepository;
import infra.repository.meeting.MeetingRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
@Import(JdbcMeetingRepository.class)
class JdbcMeetingRepositoryTest {

    @Autowired JdbcMeetingRepository jdbcMeetingRepository;
    @Autowired MeetingRepository meetingRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;

    Image image;
    User user;
    Category category;
    Gathering gathering;
    Meeting meeting;

    @BeforeEach
    void beforeEach() {
        image = returnDummyImage(1);
        user = returnDummyUser(1, image);
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, user, image);
        meeting = returnDummyMeeting(1, user, gathering, image);
    }

    private void saveBaseData() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.save(user);
        gatheringRepository.save(gathering);
        meetingRepository.save(meeting);
    }

    @Test
    void updateCount() {
        saveBaseData();

        jdbcMeetingRepository.updateCount(meeting.getId(), 3);

        Meeting updated = meetingRepository.findById(meeting.getId()).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(4);
    }
}
