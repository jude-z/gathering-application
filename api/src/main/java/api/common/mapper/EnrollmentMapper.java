package api.common.mapper;

import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.user.User;

import java.time.LocalDateTime;

public class EnrollmentMapper {

    public static Enrollment toEnrollment(boolean accepted, Gathering gathering, User enrolledBy) {
        return Enrollment.builder()
                .accepted(accepted)
                .gathering(gathering)
                .enrolledBy(enrolledBy)
                .date(LocalDateTime.now())
                .build();
    }
}
