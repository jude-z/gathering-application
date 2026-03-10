package api.service.enrollment;

import api.common.mapper.EnrollmentMapper;
import api.requeset.fcm.FcmRequestDto;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import api.service.alarm.AlarmService;
import api.service.fcm.FCMTokenTopicService;
import entity.alarm.Alarm;
import entity.enrollment.Enrollment;
import entity.fcm.FCMToken;
import entity.fcm.Topic;
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

import java.util.List;

import static api.requeset.fcm.FcmRequestDto.*;
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
    private final FCMTokenTopicService fcmTokenTopicService;
    private final AlarmService alarmService;

    public ApiResponse enrollGathering(Long gatheringId, Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(Status.NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedByAndTokensId(gatheringId).orElseThrow(
                    () -> new CommonException(NOT_FOUND_GATHERING));
            Enrollment exist = queryDslEnrollmentRepository.existEnrollment(gatheringId, userId);
            if(exist != null) throw new CommonException(ALREADY_ENROLLMENT);
            Enrollment enrollment = EnrollmentMapper.toEnrollment(false, gathering, user);
            enrollmentRepository.save(enrollment);
            String title = "enrollment";
            String content = "%s has enrolled gathering".formatted(user.getNickname());
            FcmRequestDto.TokenNotificationRequestDto tokenNotificationRequestDto = FcmRequestDto.TokenNotificationRequestDto.from(title,content);
            User createBy = gathering.getCreateBy();
            List<FCMToken> tokens = createBy.getTokens();
            fcmTokenTopicService.sendByToken(tokenNotificationRequestDto, tokens);
            String alarmContent = "%s has enrolled gathering".formatted(user.getNickname());
            Alarm alarm = Alarm.from(alarmContent, createBy);
            alarmService.save(alarm);
            return ApiStatusResponse.of(SUCCESS);
    }
    public ApiResponse disEnrollGathering(Long gatheringId, Long userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedAndTopicBy(gatheringId)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
            Enrollment enrollment = queryDslEnrollmentRepository.findEnrollment(gatheringId, user.getId(),true)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_ENROLLMENT));
            Long createdById = gathering.getCreateBy().getId();
            if(ObjectUtils.nullSafeEquals(createdById,userId)) throw new CommonException(NOT_DIS_ENROLLMENT);
            enrollmentRepository.delete(enrollment);
            jdbcGatheringRepository.updateCount(gatheringId,-1);
            Topic topic = gathering.getTopic();
            String topicName = topic.getTopicName();
            fcmTokenTopicService.unsubscribeFromTopic(topicName,userId);
            return ApiStatusResponse.of(SUCCESS);

    }


    public ApiResponse permit(Long gatheringId, Long enrollmentId,Long userId) {
            userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedAndTopicBy(gatheringId)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
            Enrollment enrollment = queryDslEnrollmentRepository.findEnrollmentEnrolledByAndTokensById(enrollmentId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_ENROLLMENT));
            Long createdById = gathering.getCreateBy().getId();
            if(!createdById.equals(userId)) throw new CommonException(NOT_AUTHORIZE);
            enrollment.changeAccepted();
            User enrolledBy = enrollment.getEnrolledBy();
            Long enrolledById = enrolledBy.getId();
            List<FCMToken> tokens = enrolledBy.getTokens();
            jdbcGatheringRepository.updateCount(gatheringId,1);
            String title = "permit";
            String content = "permit Gathering";
            TokenNotificationRequestDto tokenNotificationRequestDto = TokenNotificationRequestDto.from(title, content);
            fcmTokenTopicService.sendByToken(tokenNotificationRequestDto, tokens);
            Topic topic = gathering.getTopic();
            String topicName = topic.getTopicName();
            fcmTokenTopicService.subscribeToTopic(topicName,enrolledById);
            String alarmContent = "permit Gathering";
            Alarm alarm = Alarm.from(alarmContent, enrolledBy);
            alarmService.save(alarm);
            return ApiStatusResponse.of(SUCCESS);

    }
}
