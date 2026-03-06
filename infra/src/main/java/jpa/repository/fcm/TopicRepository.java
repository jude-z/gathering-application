package jpa.repository.fcm;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.myproject.entity.fcm.Topic;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByTopicName(String topicName);
}
