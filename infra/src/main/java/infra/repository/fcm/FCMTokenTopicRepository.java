package infra.repository.fcm;

import entity.fcm.FCMToken;
import entity.fcm.FCMTokenTopic;
import entity.fcm.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FCMTokenTopicRepository extends JpaRepository<FCMTokenTopic, Long> {

    List<FCMTokenTopic> findByFcmTokenIn(List<FCMToken> tokens);

}
