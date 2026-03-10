package entity.fcm;

import entity.gathering.Gathering;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "topic_name")
    private String topicName;
    @OneToOne(mappedBy = "topic", optional = false)
    private Gathering gathering;

    @Builder
    private Topic(String topicName, Gathering gathering) {
        this.topicName = topicName;
        this.gathering = gathering;
    }
}

