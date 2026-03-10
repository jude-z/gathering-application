package infra.repository.recommend;

import entity.recommend.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface RecommendRepository extends JpaRepository<Recommend,Long> {
}
