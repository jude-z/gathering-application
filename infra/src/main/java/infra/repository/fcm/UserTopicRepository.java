package infra.repository.fcm;

import entity.fcm.Topic;
import entity.fcm.UserTopic;
import entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserTopicRepository extends JpaRepository<UserTopic, Long> {

    List<UserTopic> findByUser(User user);

}
