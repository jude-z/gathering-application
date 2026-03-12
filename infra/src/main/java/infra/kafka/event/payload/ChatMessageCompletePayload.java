package infra.kafka.event.payload;

import entity.outbox.EventType;
import infra.kafka.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageCompletePayload extends EventPayload {

    private EventType type;
    private Long chatRoomId;
    private Long publisherId;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    private ChatMessageCompletePayload(EventType type, Long chatRoomId, Long publisherId, String content, LocalDateTime createdAt) {
        this.type = type;
        this.chatRoomId = chatRoomId;
        this.publisherId = publisherId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
