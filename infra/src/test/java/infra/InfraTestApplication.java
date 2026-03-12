package infra;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "entity.alarm", "entity.attend", "entity.board", "entity.category",
        "entity.certification", "entity.chat", "entity.enrollment",
        "entity.gathering", "entity.image", "entity.like", "entity.meeting",
        "entity.recommend", "entity.user"
})
@EnableJpaRepositories(
        basePackages = "infra.repository",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "infra\\.repository\\.outbox\\..*")
)
public class InfraTestApplication {
}
