package api.service.enrollment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;
    private final FCMTokenTopicService fcmTokenTopicService;
    private final AlarmService alarmService;
    private final RecommendService recommendService;
    public EnrollGatheringResponse enrollGathering(Long gatheringId, Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Gathering gathering = gatheringRepository.findGatheringFetchCreatedByAndTokensId(gatheringId).orElseThrow(
                    () -> new NotFoundGatheringException("no exist Gathering!!"));
            Enrollment exist = enrollmentRepository.existEnrollment(gatheringId, userId);
            if(exist != null) throw new AlreadyEnrollmentException("Already enrolled;");
            Enrollment enrollment = Enrollment.of(false,gathering,user,LocalDateTime.now());
            enrollmentRepository.save(enrollment);
            recommendService.addScore(gatheringId,1);
            String title = "enrollment";
            String content = "%s has enrolled gathering".formatted(user.getNickname());
            TokenNotificationRequestDto tokenNotificationRequestDto = TokenNotificationRequestDto.from(title,content);
            User createBy = gathering.getCreateBy();
            List<FCMToken> tokens = createBy.getTokens();
            fcmTokenTopicService.sendByToken(tokenNotificationRequestDto, tokens);
            String alarmContent = "%s has enrolled gathering".formatted(user.getNickname());
            Alarm alarm = Alarm.from(alarmContent, createBy);
            alarmService.save(alarm);
            return EnrollGatheringResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }
    public DisEnrollGatheringResponse disEnrollGathering(Long gatheringId, Long userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Gathering gathering = gatheringRepository.findGatheringFetchCreatedAndTopicBy(gatheringId)
                    .orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
            Enrollment enrollment = enrollmentRepository.findEnrollment(gatheringId, user.getId(),true)
                    .orElseThrow(() ->  new NotFoundEnrollmentException("no exist Enrollment!!"));
            Long createdById = gathering.getCreateBy().getId();
            if(ObjectUtils.nullSafeEquals(createdById,userId)) throw new NotDisEnrollmentException("Opener cannot disEnroll!!");
            enrollmentRepository.delete(enrollment);
            gatheringRepository.updateCount(gatheringId,-1);
            Topic topic = gathering.getTopic();
            String topicName = topic.getTopicName();
            fcmTokenTopicService.unsubscribeFromTopic(topicName,userId);
            recommendService.addScore(gatheringId,-1);
            return DisEnrollGatheringResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }


    public PermitEnrollmentResponse permit(Long gatheringId, Long enrollmentId,Long userId) {
            userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Gathering gathering = gatheringRepository.findGatheringFetchCreatedAndTopicBy(gatheringId)
                    .orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
            Enrollment enrollment = enrollmentRepository.findEnrollmentEnrolledByAndTokensById(enrollmentId)
                    .orElseThrow(()-> new NotFoundEnrollmentException("no exist Enrollment!!"));
            Long createdById = gathering.getCreateBy().getId();
            if(!createdById.equals(userId)) throw new NotAuthorizeException("Not authorized");
            enrollment.changeAccepted();
            User enrolledBy = enrollment.getEnrolledBy();
            Long enrolledById = enrolledBy.getId();
            List<FCMToken> tokens = enrolledBy.getTokens();
            gatheringRepository.updateCount(gatheringId,1);
            recommendService.addScore(gatheringId,1);
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
            return PermitEnrollmentResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }
}
