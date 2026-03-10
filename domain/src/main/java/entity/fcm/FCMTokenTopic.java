package entity.fcm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "fcm_token_topic")
public class FCMTokenTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fcm_token_id")
    private FCMToken fcmToken;

    @Builder
    private FCMTokenTopic(Topic topic, FCMToken fcmToken) {
        this.topic = topic;
        this.fcmToken = fcmToken;
    }
}
