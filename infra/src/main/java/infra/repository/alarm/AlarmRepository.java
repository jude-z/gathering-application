package infra.repository.alarm;

import entity.alarm.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
}
