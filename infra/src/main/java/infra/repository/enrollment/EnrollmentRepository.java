package infra.repository.enrollment;

import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    Optional<Enrollment> findByGatheringAndEnrolledBy(Gathering gathering, User enrolledBy);
}
