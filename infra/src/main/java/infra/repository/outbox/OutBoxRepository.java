package infra.repository.outbox;

import entity.outbox.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutBoxRepository extends JpaRepository<OutBox,Long> {
}
