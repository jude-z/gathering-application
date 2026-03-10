package querydsl.repository.alarm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.alarm.Alarm;
import entity.image.Image;
import entity.user.User;
import infra.repository.alarm.QueryDslAlarmRepository;
import jakarta.persistence.EntityManager;
import infra.repository.alarm.AlarmRepository;
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
class QueryDslAlarmRepositoryTest {

    QueryDslAlarmRepository queryDslAlarmRepository;

    @Autowired EntityManager em;
    @Autowired AlarmRepository alarmRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;

    Image image;
    User user;

    @BeforeEach
    void beforeEach() {
        queryDslAlarmRepository = new QueryDslAlarmRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        user = returnDummyUser(1, image);
        imageRepository.save(image);
        userRepository.save(user);
    }

    @Test
    void findUncheckedAlarm() {
        alarmRepository.save(returnDummyAlarm(1, user, false));
        alarmRepository.save(returnDummyAlarm(2, user, false));
        alarmRepository.save(returnDummyAlarm(3, user, true));

        QueryDslPageResponse<Alarm> result = queryDslAlarmRepository
                .findUncheckedAlarm(PageableInfo.of(0, 10), user.getId());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalCount()).isEqualTo(2);
    }

    @Test
    void findCheckedAlarm() {
        alarmRepository.save(returnDummyAlarm(1, user, false));
        alarmRepository.save(returnDummyAlarm(2, user, true));
        alarmRepository.save(returnDummyAlarm(3, user, true));

        QueryDslPageResponse<Alarm> result = queryDslAlarmRepository
                .findCheckedAlarm(PageableInfo.of(0, 10), user.getId());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalCount()).isEqualTo(2);
    }

    @Test
    void findUncheckedAlarm_empty() {
        QueryDslPageResponse<Alarm> result = queryDslAlarmRepository
                .findUncheckedAlarm(PageableInfo.of(0, 10), user.getId());

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalCount()).isEqualTo(0);
    }
}
