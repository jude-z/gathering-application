package infra.kafka.event.payload;

import entity.outbox.EventType;
import infra.kafka.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePayload extends EventPayload {

    private EventType type;
    private Long chatRoomId;
    private Long publisherId;
    private String content;
    private LocalDateTime createdAt;

}
