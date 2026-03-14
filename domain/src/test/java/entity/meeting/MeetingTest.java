package entity.meeting;

import entity.image.Image;
import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MeetingTest {

    @Test
    @DisplayName("Builder로 Meeting 정상 생성 - attends 빈 리스트 초기화")
    void builder_정상_생성() {
        User user = User.builder().username("creator").password("pw").role(Role.USER).nickname("nick").build();
        LocalDateTime meetingDate = LocalDateTime.of(2025, 3, 1, 14, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 3, 1, 16, 0);

        Meeting meeting = Meeting.builder()
                .title("Test Meeting")
                .meetingDate(meetingDate)
                .endDate(endDate)
                .content("Meeting Content")
                .createdBy(user)
                .count(5)
                .build();

        assertThat(meeting.getTitle()).isEqualTo("Test Meeting");
        assertThat(meeting.getMeetingDate()).isEqualTo(meetingDate);
        assertThat(meeting.getEndDate()).isEqualTo(endDate);
        assertThat(meeting.getContent()).isEqualTo("Meeting Content");
        assertThat(meeting.getCreatedBy()).isEqualTo(user);
        assertThat(meeting.getCount()).isEqualTo(5);
        assertThat(meeting.getAttends()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("changeMeeting() 호출 시 모임 정보 변경")
    void changeMeeting_정보_변경() {
        Meeting meeting = Meeting.builder()
                .title("Old Title")
                .content("Old Content")
                .meetingDate(LocalDateTime.of(2025, 3, 1, 14, 0))
                .endDate(LocalDateTime.of(2025, 3, 1, 16, 0))
                .count(5)
                .build();

        LocalDateTime newMeetingDate = LocalDateTime.of(2025, 4, 1, 10, 0);
        LocalDateTime newEndDate = LocalDateTime.of(2025, 4, 1, 12, 0);
        Image newImage = Image.builder().url("new-url").contentType("image/png").build();

        meeting.changeMeeting("New Title", "New Content", newMeetingDate, newEndDate, newImage);

        assertThat(meeting.getTitle()).isEqualTo("New Title");
        assertThat(meeting.getContent()).isEqualTo("New Content");
        assertThat(meeting.getMeetingDate()).isEqualTo(newMeetingDate);
        assertThat(meeting.getEndDate()).isEqualTo(newEndDate);
        assertThat(meeting.getImage()).isEqualTo(newImage);
    }
}
