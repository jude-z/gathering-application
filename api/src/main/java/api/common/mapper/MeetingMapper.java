package api.common.mapper;

import api.requeset.meeting.MeetingRequestDto.AddMeetingRequest;
import api.requeset.meeting.MeetingRequestDto.UpdateMeetingRequest;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.meeting.Meeting;
import entity.user.User;

public class MeetingMapper {
    public static Meeting toMeeting(AddMeetingRequest addMeetingRequest, Image image, User user, Gathering gathering) {
        return Meeting.builder()
                .title(addMeetingRequest.getTitle())
                .content(addMeetingRequest.getContent())
                .meetingDate(addMeetingRequest.getMeetingDate())
                .endDate(addMeetingRequest.getEndDate())
                .createdBy(user)
                .gathering(gathering)
                .image(image)
                .count(1)
                .build();
    }

    public static void updateMeeting(Meeting meeting, UpdateMeetingRequest updateMeetingRequest, Image image) {
        meeting.changeMeeting(
                updateMeetingRequest.getTitle(),
                updateMeetingRequest.getContent(),
                updateMeetingRequest.getMeetingDate(),
                updateMeetingRequest.getEndDate(),
                image
        );
    }
}
