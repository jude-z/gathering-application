package querydsl.repository.meeting;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import infra.dto.querydsl.meeting.MeetingProjection;
import infra.dto.querydsl.meeting.MeetingsProjection;
import entity.attend.Attend;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.meeting.Meeting;
import entity.user.User;
import infra.repository.meeting.QueryDslMeetingRepository;
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
class QueryDslMeetingRepositoryTest {

    QueryDslMeetingRepository queryDslMeetingRepository;

    @Autowired EntityManager em;
    @Autowired MeetingRepository meetingRepository;
    @Autowired AttendRepository attendRepository;
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
        queryDslMeetingRepository = new QueryDslMeetingRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);

        imageRepository.save(image);
        userRepository.saveAll(users);
        categoryRepository.save(category);

        gathering = returnDummyGathering(1, category, users.get(0), image);
        gatheringRepository.save(gathering);

        meeting = returnDummyMeeting(1, users.get(0), gathering, image);
        meetingRepository.save(meeting);
    }

    @Test
    void meetingDetail() {
        Attend attend = returnDummyAttend(users.get(1), meeting);
        attendRepository.save(attend);

        QueryDslPageResponse<MeetingProjection> result = queryDslMeetingRepository
                .meetingDetail(PageableInfo.of(0, 10), meeting.getId());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title1");
    }

    @Test
    void meetingDetail_noAttendees() {
        QueryDslPageResponse<MeetingProjection> result = queryDslMeetingRepository
                .meetingDetail(PageableInfo.of(0, 10), meeting.getId());

        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    void meetings() {
        QueryDslPageResponse<MeetingsProjection> result = queryDslMeetingRepository
                .meetings(PageableInfo.of(0, 10), gathering.getId());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalCount()).isEqualTo(1);
    }

    @Test
    void meetings_empty() {
        QueryDslPageResponse<MeetingsProjection> result = queryDslMeetingRepository
                .meetings(PageableInfo.of(0, 10), 999L);

        assertThat(result.getContent()).isEmpty();
    }
}
