package api.requeset.fcm;

import entity.fcm.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FcmRequestDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TokenNotificationRequestDto {
        private String title;
        private String content;
        private String url;
        private String img;

        public static TokenNotificationRequestDto from(String title,String content){
            return TokenNotificationRequestDto.builder()
                    .title(title)
                    .content(content)
                    .url(null)
                    .img(null)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TopicNotificationRequestDto {
        private String title;
        private String content;
        private String url;
        private String img;
        private String topic;

        public static TopicNotificationRequestDto from(String title, String content, Topic topic){
            return TopicNotificationRequestDto.builder()
                    .topic(topic.getTopicName())
                    .title(title)
                    .content(content)
                    .url(null)
                    .img(null)
                    .build();
        }
    }


}
