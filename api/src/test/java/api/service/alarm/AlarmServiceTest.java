package api.service.alarm;

import api.response.ApiDataResponse;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import entity.alarm.Alarm;
import entity.user.User;
import exception.CommonException;
import infra.repository.alarm.AlarmRepository;
import infra.repository.alarm.QueryDslAlarmRepository;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @InjectMocks
    private AlarmService alarmService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private QueryDslAlarmRepository queryDslAlarmRepository;

    @Test
    @DisplayName("checkAlarm 성공 시 알람이 checked 처리된다")
    void checkAlarm_성공() {
        User user = User.builder().username("user").password("pw").nickname("nick").build();
        Alarm alarm = Alarm.builder().content("test").checked(false).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(alarmRepository.findById(1L)).willReturn(Optional.of(alarm));

        ApiResponse response = alarmService.checkAlarm(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        assertThat(alarm.isChecked()).isTrue();
    }

    @Test
    @DisplayName("checkAlarm 사용자 없으면 CommonException")
    void checkAlarm_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> alarmService.checkAlarm(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("checkAlarm 알람 없으면 CommonException")
    void checkAlarm_알람없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(alarmRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> alarmService.checkAlarm(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("deleteAlarm 성공")
    void deleteAlarm_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Alarm alarm = Alarm.builder().content("test").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(alarmRepository.findById(1L)).willReturn(Optional.of(alarm));

        ApiResponse response = alarmService.deleteAlarm(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(alarmRepository).delete(alarm);
    }

    @Test
    @DisplayName("alarmList checked=true 시 확인된 알람 조회")
    void alarmList_checked_알람조회() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        QueryDslPageResponse<Alarm> pageResponse = QueryDslPageResponse.<Alarm>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslAlarmRepository.findCheckedAlarm(any(), anyLong())).willReturn(pageResponse);

        ApiResponse response = alarmService.alarmList(1, 10, 1L, true);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("alarmList checked=false 시 미확인 알람 조회")
    void alarmList_unchecked_알람조회() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        QueryDslPageResponse<Alarm> pageResponse = QueryDslPageResponse.<Alarm>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslAlarmRepository.findUncheckedAlarm(any(), anyLong())).willReturn(pageResponse);

        ApiResponse response = alarmService.alarmList(1, 10, 1L, false);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("save 호출 시 알람 저장")
    void save_성공() {
        Alarm alarm = Alarm.builder().content("test").build();

        alarmService.save(alarm);

        verify(alarmRepository).save(alarm);
    }
}
