package infra.kafka.event;

import infra.kafka.event.payload.ChatMessagePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxTest {

    @Test
    @DisplayName("create() - Event로부터 Outbox 생성")
    void create_이벤트로부터_생성() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(2L)
                .content("Hello")
                .build();
        payload.setEventId(100L);

        Event<EventPayload> event = Event.of(EventType.CHAT_MESSAGE, payload);
        LocalDateTime now = LocalDateTime.of(2025, 3, 1, 12, 0);

        Outbox outbox = Outbox.create(event, now);

        assertThat(outbox.getOutboxId()).isEqualTo(100L);
        assertThat(outbox.getEventType()).isEqualTo(EventType.CHAT_MESSAGE);
        assertThat(outbox.getPayload()).isNotNull();
        assertThat(outbox.getCreatedAt()).isEqualTo(now);
        assertThat(outbox.isProcessed()).isFalse();
    }
}
