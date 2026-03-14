package entity.alarm;

import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AlarmTest {

    @Test
    @DisplayName("Builder로 Alarm 정상 생성")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        Alarm alarm = Alarm.builder()
                .content("New alarm")
                .user(user)
                .date(now)
                .checked(false)
                .build();

        assertThat(alarm.getContent()).isEqualTo("New alarm");
        assertThat(alarm.getUser()).isEqualTo(user);
        assertThat(alarm.getDate()).isEqualTo(now);
        assertThat(alarm.isChecked()).isFalse();
    }

    @Test
    @DisplayName("setChecked() 호출 시 checked가 true로 변경")
    void setChecked_확인_처리() {
        Alarm alarm = Alarm.builder()
                .content("Alarm")
                .checked(false)
                .date(LocalDateTime.now())
                .build();

        alarm.setChecked();

        assertThat(alarm.isChecked()).isTrue();
    }
}
