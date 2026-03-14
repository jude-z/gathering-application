package infra.kafka.event;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Outbox {
    @Id
    @Column(name = "outbox_id")
    private Long outboxId;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String payload;
    private LocalDateTime createdAt;
    private boolean processed;

    @Builder
    private Outbox(Long outboxId, EventType eventType, String payload, LocalDateTime createdAt) {
        this.outboxId = outboxId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.processed = false;
    }

    public static Outbox create(Event event,LocalDateTime localDateTime) {

        EventType eventType = event.getType();
        EventPayload payload = event.getPayload();
        Long eventId = payload.getEventId();
        String serializedPayload = DataSerializer.serialize(payload);

        return Outbox.builder()
                .eventType(eventType)
                .payload(serializedPayload)
                .createdAt(localDateTime)
                .outboxId(eventId)
                .build();
    }
}
