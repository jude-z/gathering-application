package infra.kafka.event;

import infra.kafka.event.payload.ChatMessagePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    @DisplayName("of() - 팩토리 메서드로 이벤트 생성")
    void of_이벤트_생성() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(2L)
                .content("Hello")
                .createdAt(LocalDateTime.of(2025, 3, 1, 12, 0))
                .build();

        Event<EventPayload> event = Event.of(EventType.CHAT_MESSAGE, payload);

        assertThat(event.getType()).isEqualTo(EventType.CHAT_MESSAGE);
        assertThat(event.getPayload()).isEqualTo(payload);
    }

    @Test
    @DisplayName("fromJson() - JSON 문자열로부터 이벤트 복원")
    void fromJson_이벤트_복원() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(2L)
                .content("Hello")
                .createdAt(LocalDateTime.of(2025, 3, 1, 12, 0))
                .build();
        Event<EventPayload> original = Event.of(EventType.CHAT_MESSAGE, payload);
        String json = DataSerializer.serialize(original);

        Event restored = Event.fromJson(json);

        assertThat(restored).isNotNull();
        assertThat(restored.getType()).isEqualTo(EventType.CHAT_MESSAGE);
        ChatMessagePayload restoredPayload = (ChatMessagePayload) restored.getPayload();
        assertThat(restoredPayload.getChatRoomId()).isEqualTo(1L);
        assertThat(restoredPayload.getContent()).isEqualTo("Hello");
    }

    @Test
    @DisplayName("fromJson() - 잘못된 JSON은 null 반환")
    void fromJson_잘못된_JSON() {
        Event result = Event.fromJson("invalid json");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("EventType - 토픽 매핑 확인")
    void eventType_토픽_매핑() {
        assertThat(EventType.CHAT_MESSAGE.getTopic()).isEqualTo("chat-message");
        assertThat(EventType.CHAT_MESSAGE_COMPLETE.getTopic()).isEqualTo("chat-message-complete");
    }

    @Test
    @DisplayName("EventType - payload 클래스 매핑 확인")
    void eventType_payload_클래스_매핑() {
        assertThat(EventType.CHAT_MESSAGE.getPayloadClass()).isEqualTo(ChatMessagePayload.class);
    }
}
