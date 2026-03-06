package api.service.meeting;

import api.response.ApiResponse;
import api.service.alarm.AlarmService;
import api.service.image.ImageUploadService;
import api.service.recommend.RecommendService;
import jpa.repository.attend.AttendRepository;
import jpa.repository.gathering.GatheringRepository;
import jpa.repository.image.ImageRepository;
import jpa.repository.meeting.MeetingRepository;
import jpa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final GatheringRepository gatheringRepository;
    private final AttendRepository attendRepository;
    private final ImageUploadService imageUploadService;
    private final ImageRepository imageRepository;
    private final RecommendService recommendService;
//    private final AsyncService asyncService;
    private final AlarmService alarmService;
    @Value("${server.url}")
    private String url;
    public ApiResponse addMeeting(AddMeetingRequest addMeetingRequest, Long userId, Long gatheringId, MultipartFile file) throws IOException {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Gathering gathering = gatheringRepository.findTopicById(gatheringId)
                    .orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
            Image image = null;
            image = saveImage(image,file);
            Meeting meeting = Meeting.of(addMeetingRequest,image,user,gathering);
            Attend attend = Attend.of(meeting,user);
            if(image!=null) imageRepository.save(image);
            meetingRepository.save(meeting);
            attendRepository.save(attend);
            recommendService.addScore(gatheringId,1);
            Topic topic = gathering.getTopic();
            String title = "Meeting Created";
            String content = "%s has created a new meeting".formatted(user.getNickname());
            TopicNotificationRequestDto topicNotificationRequestDto = TopicNotificationRequestDto.from(title, content, topic);
            List<User> userList = userRepository.findEnrollmentById(gatheringId,userId);
            String alarmContent = "%s has created a new meeting".formatted(user.getNickname());
            List<Alarm> list = getAlarmList(userList,alarmContent);
            alarmService.saveAll(list);
            asyncService.sendTopic(topicNotificationRequestDto);
            return AddMeetingResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE, meeting.getId());
    }

    public ApiResponse deleteMeeting(Long userId, Long meetingId,Long gatheringId) {

            userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(()->new NotFoundMeetingExeption("no exist Meeting!!"));
            User createdBy = meeting.getCreatedBy();
            boolean authorize = ObjectUtils.nullSafeEquals(createdBy.getId(),userId);
            if(!authorize) throw new NotAuthorizeException("no authority!");
            meetingRepository.delete(meeting);
            recommendService.addScore(gatheringId,-1);
            return DeleteMeetingResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public ApiResponse updateMeeting(UpdateMeetingRequest updateMeetingRequest, Long userId, Long meetingId, MultipartFile file,Long gatheringId) throws IOException {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Gathering gathering = gatheringRepository.findTopicById(gatheringId)
                    .orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(()->new NotFoundMeetingExeption("no exist Meeting!!"));
            boolean authorize = Objects.equals(meeting.getCreatedBy().getId(), user.getId());
            if(!authorize) throw new NotAuthorizeException("no authority!");
            Image image = null;
            image = saveImage(image,file);
            if(image!=null) imageRepository.save(image);
            if(!meeting.getMeetingDate().equals(updateMeetingRequest.getMeetingDate())){
                Topic topic = gathering.getTopic();
                String title = "Meeting Updated";
                String content = "%s has changed meeting date : %s".formatted(user.getNickname(),meeting.getMeetingDate());
                TopicNotificationRequestDto topicNotificationRequestDto = TopicNotificationRequestDto.from(title, content, topic);
                List<User> userList = userRepository.findEnrollmentById(gatheringId, userId);
                String alarmContent = "%s has changed meeting date : %s".formatted(user.getNickname(),meeting.getMeetingDate());
                List<Alarm> alarmList = getAlarmList(userList, alarmContent);
                alarmService.saveAll(alarmList);
                asyncService.sendTopic(topicNotificationRequestDto);
            }
            changeMeeting(meeting,updateMeetingRequest,image);
            return UpdateMeetingResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,meetingId);
    }

    public ApiResponse meetingDetail(Long meetingId,Long gatheringId) {

        List<MeetingDetailQuery> meetingDetailQueries = meetingRepository.meetingDetail(meetingId);
        if(meetingDetailQueries.isEmpty()) throw new NotFoundMeetingExeption("no exist Meeting!!");
        return toMeetingResponse(meetingDetailQueries);
    }
    public MeetingsResponse meetings(int pageNum, Long gatheringId) {
        int offset = (pageNum-1)*8;
        List<MeetingsQueryInterface> queryInterface = meetingRepository.meetings(offset,gatheringId);
        List<MeetingsQuery> list = queryInterface.stream().map(query -> MeetingsQuery.from(query,url))
                .toList();
        List<MeetingElement> content = convertToMeetingElements(list);
        boolean hasNext = content.size()>8;
        return MeetingsResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    private ApiResponse convertToMeetingElements(List<MeetingsQuery> queries) {
        Map<Long, MeetingElement.MeetingElementBuilder> meetingMap = new LinkedHashMap<>();

        for (MeetingsQuery query : queries) {
            MeetingElement.MeetingElementBuilder builder = meetingMap.get(query.getId());

            if (builder == null) {
                builder = MeetingElement.builder()
                        .id(query.getId())
                        .title(query.getTitle())
                        .createdBy(query.getCreatedBy())
                        .meetingDate(query.getMeetingDate())
                        .endDate(query.getEndDate())
                        .content(query.getContent())
                        .count(query.getCount())
                        .url(query.getUrl())
                        .participatedList(new ArrayList<>());

                meetingMap.put(query.getId(), builder);
            }
            if (query.getParticipatedImageUrl() != null &&
                    query.getParticipatedId() != null) {
                builder.build().getParticipatedList().add(new Participated(query.getParticipatedId(),query.getParticipatedImageUrl()));
            }
        }

        return meetingMap.values().stream()
                .map(MeetingElement.MeetingElementBuilder::build)
                .collect(Collectors.toList());
    }

    private ApiResponse toMeetingResponse(List<MeetingDetailQuery> meetingDetailQueries) {
        List<String> attendBy = meetingDetailQueries.stream().map(MeetingDetailQuery::getAttendedBy).toList();
        List<String> attendByNickname = meetingDetailQueries.stream().map(MeetingDetailQuery::getAttendByNickname).toList();
        List<String> attendByUrl = meetingDetailQueries.stream().map(query -> url + query.getAttendedByUrl()).toList();

        return MeetingResponse.builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .id(meetingDetailQueries.getFirst().getId())
                .title(meetingDetailQueries.getFirst().getTitle())
                .createdBy(meetingDetailQueries.getFirst().getCreatedBy())
                .createdByNickname(meetingDetailQueries.getFirst().getCreatedByNickname())
                .createdByUrl(url+meetingDetailQueries.getFirst().getCreatedByUrl())
                .endDate(meetingDetailQueries.getFirst().getEndDate())
                .endDate(meetingDetailQueries.getFirst().getEndDate())
                .content(meetingDetailQueries.getFirst().getContent())
                .meetingUrl(url+meetingDetailQueries.getFirst().getUrl())
                .attendedBy(attendBy)
                .attendedByNickname(attendByNickname)
                .attendedByUrl(attendByUrl)
                .build();
    }

    private Image saveImage(Image image, MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()){
            String url = s3ImageUploadService.upload(file);
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
