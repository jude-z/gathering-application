package api.common.mapper;

import entity.attend.Attend;
import entity.meeting.Meeting;
import entity.user.User;

import java.time.LocalDateTime;

public class AttendMapper {
    public static Attend toAttend(Meeting meeting, User user) {
        return Attend.builder()
                .meeting(meeting)
                .attendBy(user)
                .date(LocalDateTime.now())
                .build();
    }
}
