package api.service.attend;

import api.response.ApiResponse;
import api.service.recommend.RecommendService;
import entity.attend.Attend;
import entity.gathering.Gathering;
import entity.meeting.Meeting;
import entity.user.User;
import exception.CommonException;
import exception.Status;
import jpa.repository.attend.AttendRepository;
import jpa.repository.gathering.GatheringRepository;
import jpa.repository.meeting.MeetingRepository;
import jpa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import querydsl.repository.attend.QueryDslAttendRepository;
import querydsl.repository.gathering.QueryDslGatheringRepository;
import querydsl.repository.meeting.QueryDslMeetingRepository;
import querydsl.repository.user.QueryDslUserRepository;

import java.time.LocalDateTime;

import static exception.Status.*;


@Service
@Transactional
@RequiredArgsConstructor
public class AttendService {

        private final UserRepository userRepository;
        private final AttendRepository attendRepository;
        private final MeetingRepository meetingRepository;
        private final GatheringRepository gatheringRepository;
        private final QueryDslUserRepository queryDslUserRepository;
        private final QueryDslAttendRepository queryDslAttendRepository;
        private final QueryDslMeetingRepository queryDslMeetingRepository;
        private final QueryDslGatheringRepository queryDslGatheringRepository;
        private final RecommendService recommendService;
//        private final AsyncService asyncService;

        public ApiResponse addAttend(Long meetingId, Long userId, Long gatheringId) {
                User user = userRepository.findById(userId)
                        .orElseThrow(()->new CommonException(NOT_FOUND_USER));
                Gathering gathering = queryDslGatheringRepository.findTopicById(gatheringId)
                        .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
                Meeting meeting = meetingRepository.findById(meetingId)
                        .orElseThrow(()->new CommonException(NOT_FOUND_MEETING));
                Attend checkAttend = queryDslAttendRepository.findByUserIdAndMeetingId(user.getId(),meetingId);
                if(checkAttend != null) throw new CommonException(ALREADY_ATTEND);
                Attend attend = Attend.of(meeting,user,LocalDateTime.now());
                attendRepository.save(attend);
                meetingRepository.updateCount(meetingId,1);
                Topic topic = gathering.getTopic();
                String title = "Board created";
                String content = "%s has created board".formatted(user.getNickname());
                TopicNotificationRequestDto topicNotificationRequestDto = TopicNotificationRequestDto.from(title,content,topic);
                asyncService.sendTopic(topicNotificationRequestDto);
                return AddAttendResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
        }

        public ApiResponse disAttend(Long meetingId, Long userId,Long gatheringId) {

                userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundUserException("no exist User!!"));
                Meeting meeting = meetingRepository.findById(meetingId)
                        .orElseThrow(()->new NotFoundMeetingExeption("no exist Meeting!!"));
                Attend attend = attendRepository.findByUserIdAndMeetingId(userId,meetingId);
                if(attend == null) throw  new NotFoundAttendException("Not Found Attend!!");
                Long createdById = meeting.getCreatedBy().getId();
                checkMeetingOpener(createdById, userId, meetingId, attend);
                recommendService.addScore(gatheringId,-1);
                return DisAttendResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
        }


        private void checkMeetingOpener(Long createdById, Long userId, Long meetingId, Attend attend) {
            if(!createdById.equals(userId)){
                    attendRepository.delete(attend);
                    meetingRepository.updateCount(meetingId,1);
            }else{
                throw new NotWithdrawException("cannot withdraw meeting");
            }
        }
}
