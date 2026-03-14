package api.service.meeting;

import api.response.ApiResponse;
import api.service.alarm.AlarmService;
import api.service.image.ImageUploadService;
import entity.attend.Attend;
import entity.gathering.Gathering;
import entity.meeting.Meeting;
import entity.user.User;
import exception.CommonException;
import infra.repository.attend.AttendRepository;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.meeting.MeetingProjection;
import infra.repository.dto.querydsl.meeting.MeetingsProjection;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.meeting.MeetingRepository;
import infra.repository.meeting.QueryDslMeetingRepository;
import infra.repository.user.QueryDslUserRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static api.requeset.meeting.MeetingRequestDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private AttendRepository attendRepository;
    @Mock
    private ImageUploadService imageUploadService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private AlarmService alarmService;
    @Mock
    private QueryDslUserRepository queryDslUserRepository;
    @Mock
    private QueryDslGatheringRepository queryDslGatheringRepository;
    @Mock
    private QueryDslMeetingRepository queryDslMeetingRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(meetingService, "url", "http://localhost:8080");
    }

    @Test
    @DisplayName("addMeeting 성공 - 이미지 없음")
    void addMeeting_성공() throws IOException {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(user).build();
        AddMeetingRequest request = AddMeetingRequest.builder()
                .title("meeting").content("content")
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingDate(LocalDateTime.now().plusDays(2))
                .build();
        QueryDslPageResponse<User> userPage = QueryDslPageResponse.<User>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslUserRepository.findEnrollmentById(1L, 1L)).willReturn(userPage);

        ApiResponse response = meetingService.addMeeting(request, 1L, 1L, null);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(meetingRepository).save(any(Meeting.class));
        verify(attendRepository).save(any(Attend.class));
    }

    @Test
    @DisplayName("addMeeting 사용자 없으면 CommonException")
    void addMeeting_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        AddMeetingRequest request = AddMeetingRequest.builder()
                .title("m").content("c")
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingDate(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> meetingService.addMeeting(request, 1L, 1L, null))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addMeeting 모임 없으면 CommonException")
    void addMeeting_모임없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.empty());
        AddMeetingRequest request = AddMeetingRequest.builder()
                .title("m").content("c")
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingDate(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> meetingService.addMeeting(request, 1L, 1L, null))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addMeeting 이미지 포함 성공")
    void addMeeting_이미지포함() throws IOException {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(user).build();
        AddMeetingRequest request = AddMeetingRequest.builder()
                .title("meeting").content("content")
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingDate(LocalDateTime.now().plusDays(2))
                .build();
        MultipartFile file = mock(MultipartFile.class);
        given(file.isEmpty()).willReturn(false);
        given(file.getContentType()).willReturn("image/png");
        given(imageUploadService.upload(file)).willReturn("uploaded.png");
        QueryDslPageResponse<User> userPage = QueryDslPageResponse.<User>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslUserRepository.findEnrollmentById(1L, 1L)).willReturn(userPage);

        ApiResponse response = meetingService.addMeeting(request, 1L, 1L, file);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(imageRepository).save(any());
    }

    @Test
    @DisplayName("deleteMeeting 성공")
    void deleteMeeting_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Meeting meeting = Meeting.builder().title("m").content("c").createdBy(user).build();
        ReflectionTestUtils.setField(meeting, "id", 1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));

        ApiResponse response = meetingService.deleteMeeting(1L, 1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(meetingRepository).delete(meeting);
    }

    @Test
    @DisplayName("deleteMeeting 권한 없으면 CommonException")
    void deleteMeeting_권한없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        User otherUser = User.builder().username("o").password("p").nickname("o").build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);
        Meeting meeting = Meeting.builder().title("m").content("c").createdBy(otherUser).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));

        assertThatThrownBy(() -> meetingService.deleteMeeting(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("deleteMeeting 일정 없으면 CommonException")
    void deleteMeeting_일정없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(meetingRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.deleteMeeting(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("updateMeeting 성공")
    void updateMeeting_성공() throws IOException {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(user).build();
        LocalDateTime meetingDate = LocalDateTime.now().plusDays(2);
        Meeting meeting = Meeting.builder().title("m").content("c").createdBy(user).meetingDate(meetingDate).build();
        UpdateMeetingRequest request = new UpdateMeetingRequest("updated", "content",
                LocalDateTime.now().plusDays(1), meetingDate);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));

        ApiResponse response = meetingService.updateMeeting(request, 1L, 1L, null, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("updateMeeting 권한 없으면 CommonException")
    void updateMeeting_권한없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        User otherUser = User.builder().username("o").password("p").nickname("o").build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(otherUser).build();
        Meeting meeting = Meeting.builder().title("m").content("c").createdBy(otherUser).build();
        UpdateMeetingRequest request = new UpdateMeetingRequest("updated", "content",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(meetingRepository.findById(1L)).willReturn(Optional.of(meeting));

        assertThatThrownBy(() -> meetingService.updateMeeting(request, 1L, 1L, null, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("meetingDetail 성공")
    void meetingDetail_성공() {
        MeetingProjection projection = MeetingProjection.builder()
                .id(1L).title("m").content("c").createdBy("u").createdByNickname("n")
                .createdByUrl("/img.png").url("/img.png")
                .attendedBy("u").attendByNickname("n").attendedByUrl("/img.png")
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
        QueryDslPageResponse<MeetingProjection> pageResponse = QueryDslPageResponse.<MeetingProjection>builder()
                .content(List.of(projection)).totalCount(1).pageNum(1).pageSize(10).totalPage(1).isEmpty(false).build();
        given(queryDslMeetingRepository.meetingDetail(1L)).willReturn(pageResponse);

        ApiResponse response = meetingService.meetingDetail(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("meetingDetail 일정 없으면 CommonException")
    void meetingDetail_일정없음() {
        QueryDslPageResponse<MeetingProjection> pageResponse = QueryDslPageResponse.<MeetingProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslMeetingRepository.meetingDetail(1L)).willReturn(pageResponse);

        assertThatThrownBy(() -> meetingService.meetingDetail(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("meetings 성공")
    void meetings_성공() {
        QueryDslPageResponse<MeetingsProjection> pageResponse = QueryDslPageResponse.<MeetingsProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslMeetingRepository.meetings(any(), eq(1L))).willReturn(pageResponse);

        ApiResponse response = meetingService.meetings(1, 10, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }
}
