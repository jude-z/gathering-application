package infra.repository.attend;

import entity.attend.Attend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendRepository extends JpaRepository<Attend,Long> {
}
