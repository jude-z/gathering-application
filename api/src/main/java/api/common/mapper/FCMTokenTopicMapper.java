package api.common.mapper;

import entity.fcm.FCMToken;
import entity.fcm.FCMTokenTopic;
import entity.fcm.Topic;

public class FCMTokenTopicMapper {
    public static FCMTokenTopic toFCMTokenTopic(Topic topic, FCMToken fcmToken) {
        return FCMTokenTopic.builder()
                .topic(topic)
                .fcmToken(fcmToken)
                .build();
    }
}
