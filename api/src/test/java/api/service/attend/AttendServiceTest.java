package api.service.attend;

import api.response.ApiResponse;
import entity.attend.Attend;
import entity.gathering.Gathering;
import entity.meeting.Meeting;
import entity.user.User;
import exception.CommonException;
import infra.repository.attend.AttendRepository;
import infra.repository.attend.QueryDslAttendRepository;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.meeting.JdbcMeetingRepository;
import infra.repository.meeting.MeetingRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttendServiceTest {

    @InjectMocks
    private AttendService attendService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AttendRepository attendRepository;
    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private QueryDslAttendRepository queryDslAttendRepository;
    @Mock
    private QueryDslGatheringRepository queryDslGatheringRepository;
    @Mock
    private JdbcMeetingRepository jdbcMeetingRepository;

    @Test
    @DisplayName("addAttend 성공")
    void addAttend_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        Meeting meeting = Meeting.builder().title("m").content("c").build();
        QueryDslPageResponse<Attend> pageResponse = QueryDslPageResponse.<Attend>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));
        given(queryDslAttendRepository.findByUserIdAndMeetingId(any(), anyLong(), anyLong())).willReturn(pageResponse);

        ApiResponse response = attendService.addAttend(1L, 1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(attendRepository).save(any(Attend.class));
    }

    @Test
    @DisplayName("addAttend 이미 참석 시 CommonException")
    void addAttend_이미참석() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        Meeting meeting = Meeting.builder().title("m").content("c").build();
        Attend attend = Attend.builder().meeting(meeting).attendBy(user).build();
        QueryDslPageResponse<Attend> pageResponse = QueryDslPageResponse.<Attend>builder()
                .content(List.of(attend)).totalCount(1).pageNum(1).pageSize(10).totalPage(1).isEmpty(false).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));
        given(queryDslAttendRepository.findByUserIdAndMeetingId(any(), anyLong(), anyLong())).willReturn(pageResponse);

        assertThatThrownBy(() -> attendService.addAttend(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("disAttend 개설자가 탈퇴 시 CommonException")
    void disAttend_개설자탈퇴불가() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Meeting meeting = Meeting.builder().title("m").content("c").createdBy(user).build();
        Attend attend = Attend.builder().meeting(meeting).attendBy(user).build();
        QueryDslPageResponse<Attend> pageResponse = QueryDslPageResponse.<Attend>builder()
                .content(List.of(attend)).totalCount(1).pageNum(1).pageSize(10).totalPage(1).isEmpty(false).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));
        given(queryDslAttendRepository.findByUserIdAndMeetingId(any(), anyLong(), anyLong())).willReturn(pageResponse);

        assertThatThrownBy(() -> attendService.disAttend(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("disAttend 참석 기록 없으면 CommonException")
    void disAttend_참석기록없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Meeting meeting = Meeting.builder().title("m").content("c").build();
        QueryDslPageResponse<Attend> pageResponse = QueryDslPageResponse.<Attend>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));
        given(queryDslAttendRepository.findByUserIdAndMeetingId(any(), anyLong(), anyLong())).willReturn(pageResponse);

        assertThatThrownBy(() -> attendService.disAttend(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addAttend 사용자 없으면 CommonException")
    void addAttend_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> attendService.addAttend(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }
}
