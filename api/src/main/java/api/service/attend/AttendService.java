package api.service.attend;

import api.common.mapper.AttendMapper;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import entity.attend.Attend;
import entity.gathering.Gathering;
import entity.meeting.Meeting;
import entity.user.User;
import exception.CommonException;
import infra.repository.meeting.JdbcMeetingRepository;
import infra.repository.attend.AttendRepository;
import infra.repository.meeting.MeetingRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import infra.repository.attend.QueryDslAttendRepository;
import infra.repository.gathering.QueryDslGatheringRepository;
import page.PageCalculator;
import page.PageableInfo;

import static exception.Status.*;


@Service
@Transactional
@RequiredArgsConstructor
public class AttendService {

        private final UserRepository userRepository;
        private final AttendRepository attendRepository;
        private final MeetingRepository meetingRepository;
        private final QueryDslAttendRepository queryDslAttendRepository;
        private final QueryDslGatheringRepository queryDslGatheringRepository;
        private final JdbcMeetingRepository jdbcMeetingRepository;

        public ApiResponse addAttend(Long meetingId, Long userId, Long gatheringId) {
                User user = userRepository.findById(userId)
                        .orElseThrow(()->new CommonException(NOT_FOUND_USER));
                Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedBy(gatheringId)
                        .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
                Meeting meeting = meetingRepository.findById(meetingId)
                        .orElseThrow(()->new CommonException(NOT_FOUND_MEETING));
                PageableInfo pageableInfo = PageCalculator.toDefaultPageableInfo();
                QueryDslPageResponse<Attend> queryDslPageResponse = queryDslAttendRepository.findByUserIdAndMeetingId(pageableInfo,user.getId(),meetingId);
                if(queryDslPageResponse.isEmpty()) throw new CommonException(ALREADY_ATTEND);
                Attend attend = AttendMapper.toAttend(meeting, user);
                attendRepository.save(attend);
                jdbcMeetingRepository.updateCount(meetingId,1);
                return ApiStatusResponse.of(SUCCESS);
        }

        public ApiResponse disAttend(Long meetingId, Long userId, Long gatheringId) {
                userRepository.findById(userId)
                        .orElseThrow(() -> new CommonException(NOT_FOUND_USER));
                Meeting meeting = meetingRepository.findById(meetingId)
                        .orElseThrow(() -> new CommonException(NOT_FOUND_MEETING));
                PageableInfo pageableInfo = PageCalculator.toDefaultPageableInfo();
                QueryDslPageResponse<Attend> queryDslPageResponse = queryDslAttendRepository
                        .findByUserIdAndMeetingId(pageableInfo, userId, meetingId);
                if (queryDslPageResponse.getContent().isEmpty()) throw new CommonException(NOT_FOUND_ATTEND);
                Attend attend = queryDslPageResponse.getContent().getFirst();
                Long createdById = meeting.getCreatedBy().getId();
                checkMeetingOpener(createdById, userId, meetingId, attend);
                if (createdById.equals(userId)) throw new CommonException(NOT_WITHDRAW);
                attendRepository.delete(attend);
                jdbcMeetingRepository.updateCount(meetingId, -1);
                return ApiStatusResponse.of(SUCCESS);
        }

        private void checkMeetingOpener(Long createdById, Long userId, Long meetingId, Attend attend) {
                if (!createdById.equals(userId)) {
                        attendRepository.delete(attend);
                        jdbcMeetingRepository.updateCount(meetingId, 1);
                } else {
                        throw new CommonException(NOT_WITHDRAW);
                }
        }
}
