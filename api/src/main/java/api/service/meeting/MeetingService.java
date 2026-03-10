package api.service.meeting;

import api.common.mapper.AttendMapper;
import api.common.mapper.MeetingMapper;
import api.response.ApiDataResponse;
import api.response.ApiResponse;
import api.service.alarm.AlarmService;
import api.service.image.ImageUploadService;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import infra.dto.querydsl.meeting.MeetingProjection;
import infra.dto.querydsl.meeting.MeetingsProjection;
import entity.alarm.Alarm;
import entity.attend.Attend;
import entity.fcm.Topic;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.meeting.Meeting;
import entity.user.User;
import exception.CommonException;
import infra.repository.attend.AttendRepository;
import infra.repository.image.ImageRepository;
import infra.repository.meeting.MeetingRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.meeting.QueryDslMeetingRepository;
import infra.repository.user.QueryDslUserRepository;
import util.PageCalculator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static api.requeset.fcm.FcmRequestDto.*;
import static api.requeset.meeting.MeetingRequestDto.*;
import static api.response.meeting.MeetingResponseDto.*;
import static exception.Status.*;


@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final AttendRepository attendRepository;
    private final ImageUploadService imageUploadService;
    private final ImageRepository imageRepository;
    private final AlarmService alarmService;
    private final QueryDslUserRepository queryDslUserRepository;
    private final QueryDslGatheringRepository queryDslGatheringRepository;
    private final QueryDslMeetingRepository queryDslMeetingRepository;
    @Value("${server.url}")
    private String url;
    public ApiResponse addMeeting(AddMeetingRequest addMeetingRequest, Long userId, Long gatheringId, MultipartFile file) throws IOException {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findTopicById(gatheringId)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
            Image image = null;
            image = saveImage(image,file);
            Meeting meeting = MeetingMapper.toMeeting(addMeetingRequest,image,user,gathering);
            Attend attend = AttendMapper.toAttend(meeting,user);
            if(image!=null) imageRepository.save(image);
            meetingRepository.save(meeting);
            attendRepository.save(attend);
            Topic topic = gathering.getTopic();
            String title = "Meeting Created";
            String content = "%s has created a new meeting".formatted(user.getNickname());
            TopicNotificationRequestDto topicNotificationRequestDto = TopicNotificationRequestDto.from(title, content, topic);
            List<User> userList = queryDslUserRepository.findEnrollmentById(gatheringId,userId).getContent();
            String alarmContent = "%s has created a new meeting".formatted(user.getNickname());
            List<Alarm> list = getAlarmList(userList,alarmContent);
            alarmService.saveAll(list);
            return ApiDataResponse.of(meeting.getId(), SUCCESS);
    }

    public ApiResponse deleteMeeting(Long userId, Long meetingId,Long gatheringId) {

            userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_MEETING));
            User createdBy = meeting.getCreatedBy();
            boolean authorize = ObjectUtils.nullSafeEquals(createdBy.getId(),userId);
            if(!authorize) throw new CommonException(NOT_AUTHORIZE);
            meetingRepository.delete(meeting);
            return ApiDataResponse.of(meeting.getId(), SUCCESS);
    }

    public ApiResponse updateMeeting(UpdateMeetingRequest updateMeetingRequest, Long userId, Long meetingId, MultipartFile file,Long gatheringId) throws IOException {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = queryDslGatheringRepository.findTopicById(gatheringId)
                    .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_MEETING));
            boolean authorize = Objects.equals(meeting.getCreatedBy().getId(), user.getId());
            if(!authorize) throw new CommonException(NOT_AUTHORIZE);
            Image image = null;
            image = saveImage(image,file);
            if(image!=null) imageRepository.save(image);
            if(!meeting.getMeetingDate().equals(updateMeetingRequest.getMeetingDate())){
                Topic topic = gathering.getTopic();
                String title = "Meeting Updated";
                String content = "%s has changed meeting date : %s".formatted(user.getNickname(),meeting.getMeetingDate());
                TopicNotificationRequestDto topicNotificationRequestDto = TopicNotificationRequestDto.from(title, content, topic);
                List<User> userList = queryDslUserRepository.findEnrollmentById(gatheringId, userId).getContent();
                String alarmContent = "%s has changed meeting date : %s".formatted(user.getNickname(),meeting.getMeetingDate());
                List<Alarm> alarmList = getAlarmList(userList, alarmContent);
                alarmService.saveAll(alarmList);
            }
            MeetingMapper.updateMeeting(meeting,updateMeetingRequest,image);
            return ApiDataResponse.of(meeting.getId(), SUCCESS);
    }

    public ApiResponse meetingDetail(Long meetingId,Long gatheringId) {

        QueryDslPageResponse<MeetingProjection> queryDslPageResponse = queryDslMeetingRepository.meetingDetail(meetingId);
        if(queryDslPageResponse.isEmpty()) throw new CommonException(NOT_FOUND_MEETING);
        MeetingResponse meetingResponse = toMeetingResponse(queryDslPageResponse);
        return ApiDataResponse.of(meetingResponse,SUCCESS);
    }
    public ApiResponse meetings(int pageNum, int pageSize,Long gatheringId) {
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<MeetingsProjection> queryDslPageResponse = queryDslMeetingRepository.meetings(pageableInfo,gatheringId);
        List<MeetingsProjection> content = queryDslPageResponse.getContent();
        List<MeetingElement> meetingElements = convertToMeetingElements(content);
        return ApiDataResponse.of(meetingElements,SUCCESS);
    }

    private List<MeetingElement> convertToMeetingElements(List<MeetingsProjection> projections) {
        Map<Long, MeetingElement> meetingMap = new LinkedHashMap<>();

        for (MeetingsProjection projection : projections) {
            MeetingElement element = meetingMap.get(projection.getId());

            if (element == null) {
                element = MeetingElement.from(projection, url);
                meetingMap.put(projection.getId(), element);
            }
            if (projection.getParticipatedId() != null &&
                    projection.getParticipatedImageUrl() != null) {
                element.getParticipatedList().add(new Participated(projection.getParticipatedId(), projection.getParticipatedImageUrl()));
            }
        }

        return new ArrayList<>(meetingMap.values());
    }

    private MeetingResponse toMeetingResponse(QueryDslPageResponse<MeetingProjection> queryDslPageResponse) {
        List<MeetingProjection> content = queryDslPageResponse.getContent();
        List<String> attendBy = content.stream().map(MeetingProjection::getAttendedBy).toList();
        List<String> attendByNickname = content.stream().map(MeetingProjection::getAttendByNickname).toList();
        List<String> attendByUrl = content.stream().map(query -> url + query.getAttendedByUrl()).toList();

        return MeetingResponse.builder()
                .id(content.getFirst().getId())
                .title(content.getFirst().getTitle())
                .createdBy(content.getFirst().getCreatedBy())
                .createdByNickname(content.getFirst().getCreatedByNickname())
                .createdByUrl(url+content.getFirst().getCreatedByUrl())
                .endDate(content.getFirst().getEndDate())
                .endDate(content.getFirst().getEndDate())
                .content(content.getFirst().getContent())
                .meetingUrl(url+content.getFirst().getUrl())
                .attendedBy(attendBy)
                .attendedByNickname(attendByNickname)
                .attendedByUrl(attendByUrl)
                .build();
    }

    private Image saveImage(Image image, MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()){
            String url = imageUploadService.upload(file);
            String contentType = file.getContentType();
            if(StringUtils.hasText(url)){
                image = Image.builder()
                        .url(url)
                        .contentType(contentType)
                        .build();
            }
        }
        return image;
    }

    private List<Alarm> getAlarmList(List<User> userList,String content) {
        return userList.stream()
                .map(user -> Alarm.builder()
                        .date(LocalDateTime.now())
                        .content(content)
                        .checked(false)
                        .user(user)
                        .build())
                .toList();
    }
}
