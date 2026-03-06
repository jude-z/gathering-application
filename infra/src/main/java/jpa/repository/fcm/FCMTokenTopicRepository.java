package jpa.repository.fcm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.myproject.entity.fcm.FCMToken;
import spring.myproject.entity.fcm.FCMTokenTopic;
import spring.myproject.entity.fcm.Topic;

import java.util.List;

public interface FCMTokenTopicRepository extends JpaRepository<FCMTokenTopic, Long> {

    void deleteByTopic(Topic topic);


    List<FCMTokenTopic> findByFcmTokenIn(List<FCMToken> tokens);


    @Modifying
    @Query("DELETE FROM FCMTokenTopic tt WHERE tt.fcmToken.tokenValue IN :tokenValues")
    void deleteByTokenValueIn(@Param("tokenValues") List<String> tokenValues);
}
