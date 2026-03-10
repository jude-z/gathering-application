package querydsl.repository.attend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.attend.Attend;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.meeting.Meeting;
import entity.user.User;
import infra.repository.attend.QueryDslAttendRepository;
import jakarta.persistence.EntityManager;
import infra.repository.attend.AttendRepository;
import infra.repository.category.CategoryRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.meeting.MeetingRepository;
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
class QueryDslAttendRepositoryTest {

    QueryDslAttendRepository queryDslAttendRepository;

    @Autowired EntityManager em;
    @Autowired AttendRepository attendRepository;
    @Autowired MeetingRepository meetingRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;
    Meeting meeting;

    @BeforeEach
    void beforeEach() {
        queryDslAttendRepository = new QueryDslAttendRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, users.get(0), image);
        meeting = returnDummyMeeting(1, users.get(0), gathering, image);

        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.save(gathering);
        meetingRepository.save(meeting);
    }

    @Test
    void findByUserIdAndMeetingId() {
        Attend attend = returnDummyAttend(users.get(0), meeting);
        attendRepository.save(attend);

        QueryDslPageResponse<Attend> result = queryDslAttendRepository
                .findByUserIdAndMeetingId(PageableInfo.of(0, 10), users.get(0).getId(), meeting.getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByUserIdAndMeetingId_notFound() {
        QueryDslPageResponse<Attend> result = queryDslAttendRepository
                .findByUserIdAndMeetingId(PageableInfo.of(0, 10), users.get(1).getId(), meeting.getId());

        assertThat(result.getContent()).isEmpty();
    }
}
