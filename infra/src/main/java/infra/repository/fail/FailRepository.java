package infra.repository.fail;

import entity.user.Fail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailRepository extends JpaRepository<Fail,Long> {

}
