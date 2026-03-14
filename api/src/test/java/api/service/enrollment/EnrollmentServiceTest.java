package api.service.enrollment;

import api.response.ApiResponse;
import api.service.alarm.AlarmService;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.user.User;
import exception.CommonException;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.enrollment.QueryDslEnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.gathering.JdbcGatheringRepository;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.user.QueryDslUserRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @InjectMocks
    private EnrollmentService enrollmentService;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private QueryDslEnrollmentRepository queryDslEnrollmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QueryDslUserRepository queryDslUserRepository;
    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private QueryDslGatheringRepository queryDslGatheringRepository;
    @Mock
    private JdbcGatheringRepository jdbcGatheringRepository;
    @Mock
    private AlarmService alarmService;

    @Test
    @DisplayName("enrollGathering 성공")
    void enrollGathering_성공() {
        User user = User.builder().username("u").password("p").nickname("nick").build();
        User creator = User.builder().username("c").password("p").nickname("creator").build();
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(creator).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.existEnrollment(1L, 1L)).willReturn(null);

        ApiResponse response = enrollmentService.enrollGathering(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(enrollmentRepository).save(any(Enrollment.class));
        verify(alarmService).save(any());
    }

    @Test
    @DisplayName("enrollGathering 사용자 없으면 CommonException")
    void enrollGathering_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollGathering(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("enrollGathering 모임 없으면 CommonException")
    void enrollGathering_모임없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollGathering(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("enrollGathering 이미 가입되어 있으면 CommonException")
    void enrollGathering_이미가입() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        User creator = User.builder().username("c").password("p").nickname("creator").build();
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(creator).build();
        Enrollment exist = Enrollment.builder().gathering(gathering).enrolledBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.existEnrollment(1L, 1L)).willReturn(exist);

        assertThatThrownBy(() -> enrollmentService.enrollGathering(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("disEnrollGathering 성공")
    void disEnrollGathering_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        User creator = User.builder().username("c").password("p").nickname("creator").build();
        ReflectionTestUtils.setField(creator, "id", 2L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(creator).build();
        Enrollment enrollment = Enrollment.builder().gathering(gathering).enrolledBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.findEnrollment(1L, 1L, true)).willReturn(Optional.of(enrollment));

        ApiResponse response = enrollmentService.disEnrollGathering(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(enrollmentRepository).delete(enrollment);
        verify(jdbcGatheringRepository).updateCount(1L, -1);
    }

    @Test
    @DisplayName("disEnrollGathering 개설자는 탈퇴 불가")
    void disEnrollGathering_개설자탈퇴불가() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(user).build();
        Enrollment enrollment = Enrollment.builder().gathering(gathering).enrolledBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.findEnrollment(1L, 1L, true)).willReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.disEnrollGathering(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("permit 성공")
    void permit_성공() {
        User creator = User.builder().username("c").password("p").nickname("creator").build();
        ReflectionTestUtils.setField(creator, "id", 1L);
        User enrolledUser = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(enrolledUser, "id", 2L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(creator).build();
        Enrollment enrollment = Enrollment.builder().gathering(gathering).enrolledBy(enrolledUser).accepted(false).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(creator));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.findEnrollmentEnrolledByAndTokensById(1L)).willReturn(Optional.of(enrollment));

        ApiResponse response = enrollmentService.permit(1L, 1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(jdbcGatheringRepository).updateCount(1L, 1);
        verify(alarmService).save(any());
    }

    @Test
    @DisplayName("permit 권한 없으면 CommonException")
    void permit_권한없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        User creator = User.builder().username("c").password("p").nickname("creator").build();
        ReflectionTestUtils.setField(creator, "id", 2L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(creator).build();
        Enrollment enrollment = Enrollment.builder().gathering(gathering).enrolledBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.findEnrollmentEnrolledByAndTokensById(1L)).willReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.permit(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("permit 가입 신청 없으면 CommonException")
    void permit_가입신청없음() {
        User creator = User.builder().username("c").password("p").nickname("creator").build();
        ReflectionTestUtils.setField(creator, "id", 1L);
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(creator).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(creator));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));
        given(queryDslEnrollmentRepository.findEnrollmentEnrolledByAndTokensById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.permit(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }
}
