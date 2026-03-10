package util;


import entity.fcm.Topic;
import entity.gathering.Gathering;

public class TopicGenerator {
    public static Topic generateTopic(Gathering gathering){
        return Topic.builder()
                .topicName(generateTopicName(gathering))
                .gathering(gathering)
                .build();
    }
    public static String generateTopicName(Gathering gathering){
        return "gathering_"+gathering.getId();
    }
}
