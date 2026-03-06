package jpa.repository.fcm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.myproject.entity.fcm.Topic;
import spring.myproject.entity.fcm.UserTopic;
import spring.myproject.entity.user.User;

import java.util.List;

public interface UserTopicRepository extends JpaRepository<UserTopic, Long> {
    List<UserTopic> findByUser(User user);
    @Query("select ut from UserTopic ut " +
            "left join fetch ut.topic left join ut.user u where u.id = :userId")
    List<UserTopic> findByUserId(Long userId);

    @Query("select count(ut)>0 from UserTopic ut join ut.topic t where t.topicName = :topicName and ut.user.id = :userId")
    boolean existsByTopicAndUser(String topicName, Long userId);

    void deleteByTopicAndUser(Topic topic, User user);
}
