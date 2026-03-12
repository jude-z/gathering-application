package api.service.enrollment;

import api.common.mapper.EnrollmentMapper;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import api.service.alarm.AlarmService;
import api.common.mapper.AlarmMapper;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.user.User;
import exception.CommonException;
import exception.Status;
import infra.repository.gathering.JdbcGatheringRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import infra.repository.enrollment.QueryDslEnrollmentRepository;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.user.QueryDslUserRepository;

import static exception.Status.*;


@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final QueryDslEnrollmentRepository queryDslEnrollmentRepository;
    private final UserRepository userRepository;
    private final QueryDslUserRepository queryDslUserRepository;
    private final GatheringRepository gatheringRepository;
    private final QueryDslGatheringRepository queryDslGatheringRepository;
    private final JdbcGatheringRepository jdbcGatheringRepository;
    private final AlarmService alarmService;

    public ApiResponse enrollGathering(Long gatheringId, Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(Status.NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedBy(gatheringId).orElseThrow(
                    () -> new CommonException(NOT_FOUND_GATHERING));
            Enrollment exist = queryDslEnrollmentRepository.existEnrollment(gatheringId, userId);
            if(exist != null) throw new CommonException(ALREADY_ENROLLMENT);
            Enrollment enrollment = EnrollmentMapper.toEnrollment(false, gathering, user);
            enrollmentRepository.save(enrollment);
            User createBy = gathering.getCreateBy();
            String alarmContent = "%s has enrolled gathering".formatted(user.getNickname());
            alarmService.save(AlarmMapper.toAlarm(alarmContent, createBy));
            return ApiStatusResponse.of(SUCCESS);
    }
    public ApiResponse disEnrollGathering(Long gatheringId, Long userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedBy(gatheringId)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
            Enrollment enrollment = queryDslEnrollmentRepository.findEnrollment(gatheringId, user.getId(),true)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_ENROLLMENT));
            Long createdById = gathering.getCreateBy().getId();
            if(ObjectUtils.nullSafeEquals(createdById,userId)) throw new CommonException(NOT_DIS_ENROLLMENT);
            enrollmentRepository.delete(enrollment);
            jdbcGatheringRepository.updateCount(gatheringId,-1);
            return ApiStatusResponse.of(SUCCESS);

    }


    public ApiResponse permit(Long gatheringId, Long enrollmentId,Long userId) {
            userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedBy(gatheringId)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
            Enrollment enrollment = queryDslEnrollmentRepository.findEnrollmentEnrolledByAndTokensById(enrollmentId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_ENROLLMENT));
            Long createdById = gathering.getCreateBy().getId();
            if(!createdById.equals(userId)) throw new CommonException(NOT_AUTHORIZE);
            enrollment.changeAccepted();
            User enrolledBy = enrollment.getEnrolledBy();
            jdbcGatheringRepository.updateCount(gatheringId,1);
            String alarmContent = "permit Gathering";
            alarmService.save(AlarmMapper.toAlarm(alarmContent, enrolledBy));
            return ApiStatusResponse.of(SUCCESS);

    }
}
