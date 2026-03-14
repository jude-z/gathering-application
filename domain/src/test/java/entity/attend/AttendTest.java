package entity.attend;

import entity.meeting.Meeting;
import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendTest {

    @Test
    @DisplayName("Builder로 Attend 정상 생성")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();
        Meeting meeting = Meeting.builder().title("Meeting").content("Content").count(5).build();
        LocalDateTime now = LocalDateTime.of(2025, 3, 1, 12, 0);

        Attend attend = Attend.builder()
                .meeting(meeting)
                .attendBy(user)
                .date(now)
                .build();

        assertThat(attend.getMeeting()).isEqualTo(meeting);
        assertThat(attend.getAttendBy()).isEqualTo(user);
        assertThat(attend.getDate()).isEqualTo(now);
    }
}
