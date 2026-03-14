package infra.support;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.alarm.Alarm;
import entity.attend.Attend;
import entity.board.Board;
import entity.category.Category;
import entity.certification.Certification;
import entity.chat.*;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.like.Like;
import entity.meeting.Meeting;
import entity.recommend.Recommend;
import entity.user.Role;
import entity.user.User;
import infra.repository.alarm.QueryDslAlarmRepository;
import infra.repository.attend.QueryDslAttendRepository;
import infra.repository.board.QueryDslBoardRepository;
import infra.repository.category.QueryDslCategoryRepository;
import infra.repository.certification.QueryDslCertificationRepository;
import infra.repository.chat.JdbcChatRepository;
import infra.repository.chat.QueryDslChatRepository;
import infra.repository.enrollment.QueryDslEnrollmentRepository;
import infra.repository.gathering.JdbcGatheringRepository;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.image.QueryDslImageRepository;
import infra.repository.like.QueryDslLikeRepository;
import infra.repository.meeting.JdbcMeetingRepository;
import infra.repository.meeting.QueryDslMeetingRepository;
import infra.repository.user.QueryDslUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RepositoryTestSupport.TestConfig.class)
public abstract class RepositoryTestSupport {

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        mysql.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @PersistenceContext
    protected EntityManager em;

    // ===== Fixture Entities =====
    protected Image profileImage1, profileImage2, gatheringImage1, meetingImage1;
    protected Image boardImage1, gatheringGalleryImage1;
    protected User user1, user2;
    protected Category category1, category2;
    protected Gathering gathering1, gathering2;
    protected Board board1;
    protected Enrollment enrollment1, enrollment2;
    protected Meeting meeting1;
    protected Attend attend1;
    protected Alarm alarm1, alarm2;
    protected Like like1;
    protected Recommend recommend1;
    protected Certification certification1;
    protected ChatRoom chatRoom1, chatRoom2;
    protected ChatParticipant chatParticipant1, chatParticipant2, chatParticipant3;
    protected ChatMessage chatMessage1, chatMessage2;
    protected ReadStatus readStatus1, readStatus2;

    @BeforeEach
    void setUpFixture() {
        // 1. Images (no FK)
        profileImage1 = persist(Image.builder().url("profile1.jpg").contentType("image/jpeg").build());
        profileImage2 = persist(Image.builder().url("profile2.jpg").contentType("image/jpeg").build());
        gatheringImage1 = persist(Image.builder().url("gathering1.jpg").contentType("image/jpeg").build());
        meetingImage1 = persist(Image.builder().url("meeting1.jpg").contentType("image/jpeg").build());

        // 2. Users
        user1 = persist(User.builder()
                .username("user1").password("pw1").email("user1@test.com")
                .address("Seoul").age(25).hobby("coding")
                .role(Role.USER).nickname("nick1").profileImage(profileImage1)
                .build());
        user2 = persist(User.builder()
                .username("user2").password("pw2").email("user2@test.com")
                .address("Busan").age(30).hobby("reading")
                .role(Role.USER).nickname("nick2").profileImage(profileImage2)
                .build());

        // 3. Categories
        category1 = persist(Category.builder().name("category1").build());
        category2 = persist(Category.builder().name("category2").build());

        // 4. Gatherings
        gathering1 = persist(Gathering.builder()
                .title("Gathering 1").content("Content 1")
                .registerDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .createBy(user1).count(10).gatheringImage(gatheringImage1).category(category1)
                .build());
        gathering2 = persist(Gathering.builder()
                .title("Gathering 2").content("Content 2")
                .registerDate(LocalDateTime.of(2025, 2, 1, 12, 0))
                .createBy(user2).count(5).category(category2)
                .build());

        // 5. Board
        board1 = persist(Board.builder()
                .user(user1).gathering(gathering1)
                .title("Board 1").description("Board Desc 1")
                .registerDate(LocalDateTime.of(2025, 1, 5, 12, 0))
                .build());

        // 6. Additional images (with FK)
        boardImage1 = persist(Image.builder().url("board1.jpg").contentType("image/jpeg").board(board1).build());
        gatheringGalleryImage1 = persist(Image.builder().url("gallery1.jpg").contentType("image/jpeg").gathering(gathering1).build());

        // 7. Enrollments
        enrollment1 = persist(Enrollment.builder()
                .accepted(true).gathering(gathering1).enrolledBy(user2)
                .date(LocalDateTime.of(2025, 1, 2, 12, 0)).build());
        enrollment2 = persist(Enrollment.builder()
                .accepted(false).gathering(gathering1).enrolledBy(user1)
                .date(LocalDateTime.of(2025, 1, 3, 12, 0)).build());

        // 8. Meeting
        meeting1 = persist(Meeting.builder()
                .title("Meeting 1")
                .meetingDate(LocalDateTime.of(2025, 3, 1, 14, 0))
                .endDate(LocalDateTime.of(2025, 3, 1, 16, 0))
                .content("Meeting Content").createdBy(user1)
                .gathering(gathering1).image(meetingImage1).count(3)
                .build());

        // 9. Attend
        attend1 = persist(Attend.builder()
                .meeting(meeting1).attendBy(user2)
                .date(LocalDateTime.of(2025, 3, 1, 14, 0)).build());

        // 10. Alarms
        alarm1 = persist(Alarm.builder()
                .content("Unchecked Alarm").user(user1)
                .date(LocalDateTime.of(2025, 1, 10, 12, 0)).checked(false).build());
        alarm2 = persist(Alarm.builder()
                .content("Checked Alarm").user(user1)
                .date(LocalDateTime.of(2025, 1, 11, 12, 0)).checked(true).build());

        // 11. Like
        like1 = persist(Like.builder().likedBy(user1).gathering(gathering1).build());

        // 12. Recommend
        recommend1 = persist(Recommend.builder().gathering(gathering1).date(LocalDate.of(2025, 3, 1)).build());

        // 13. Certification
        certification1 = persist(Certification.builder().email("user1@test.com").certification("ABC123").build());

        // 14. ChatRooms
        chatRoom1 = persist(ChatRoom.builder()
                .title("Chat Room 1").description("Chat Desc 1")
                .createdBy(user1).gathering(gathering1).count(2).build());
        chatRoom2 = persist(ChatRoom.builder()
                .title("Chat Room 2").description("Chat Desc 2")
                .createdBy(user2).gathering(gathering1).count(1).build());

        // 15. ChatParticipants
        chatParticipant1 = persist(ChatParticipant.builder().user(user1).chatRoom(chatRoom1).status(true).build());
        chatParticipant2 = persist(ChatParticipant.builder().user(user2).chatRoom(chatRoom1).status(true).build());
        chatParticipant3 = persist(ChatParticipant.builder().user(user2).chatRoom(chatRoom2).status(true).build());

        // 16. ChatMessages (manual ID)
        chatMessage1 = persist(ChatMessage.builder()
                .id(1L).content("Hello").chatRoom(chatRoom1).chatParticipant(chatParticipant1)
                .createdAt(LocalDateTime.of(2025, 3, 1, 12, 0)).build());
        chatMessage2 = persist(ChatMessage.builder()
                .id(2L).content("Hi there").chatRoom(chatRoom1).chatParticipant(chatParticipant2)
                .createdAt(LocalDateTime.of(2025, 3, 1, 12, 1)).build());

        // 17. ReadStatuses
        readStatus1 = persist(ReadStatus.builder()
                .status(false).chatParticipant(chatParticipant2).chatMessage(chatMessage1).build());
        readStatus2 = persist(ReadStatus.builder()
                .status(false).chatParticipant(chatParticipant1).chatMessage(chatMessage2).build());

        em.flush();
        em.clear();
    }

    private <T> T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    @TestConfiguration
    @EnableJpaRepositories(basePackages = "infra.repository")
    @Import({
            QueryDslAlarmRepository.class,
            QueryDslAttendRepository.class,
            QueryDslBoardRepository.class,
            QueryDslCategoryRepository.class,
            QueryDslCertificationRepository.class,
            QueryDslChatRepository.class,
            QueryDslEnrollmentRepository.class,
            QueryDslGatheringRepository.class,
            QueryDslImageRepository.class,
            QueryDslLikeRepository.class,
            QueryDslMeetingRepository.class,
            QueryDslUserRepository.class,
            JdbcChatRepository.class,
            JdbcGatheringRepository.class,
            JdbcMeetingRepository.class
    })
    static class TestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }
}
