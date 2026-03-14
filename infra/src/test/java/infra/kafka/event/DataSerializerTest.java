package infra.kafka.event;

import infra.kafka.event.payload.ChatMessagePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DataSerializerTest {

    @Test
    @DisplayName("serialize - 객체를 JSON 문자열로 변환")
    void serialize_객체를_JSON으로_변환() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(2L)
                .content("Hello")
                .createdAt(LocalDateTime.of(2025, 3, 1, 12, 0))
                .build();

        String json = DataSerializer.serialize(payload);

        assertThat(json).isNotNull();
        assertThat(json).contains("\"chatRoomId\":1");
        assertThat(json).contains("\"content\":\"Hello\"");
    }

    @Test
    @DisplayName("deserialize(String) - JSON 문자열을 객체로 변환")
    void deserialize_JSON을_객체로_변환() {
        String json = "{\"chatRoomId\":1,\"publisherId\":2,\"content\":\"Hello\"}";

        ChatMessagePayload result = DataSerializer.deserialize(json, ChatMessagePayload.class);

        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getPublisherId()).isEqualTo(2L);
        assertThat(result.getContent()).isEqualTo("Hello");
    }

    @Test
    @DisplayName("deserialize(String) - 잘못된 JSON은 null 반환")
    void deserialize_잘못된_JSON() {
        String invalidJson = "not a json";

        ChatMessagePayload result = DataSerializer.deserialize(invalidJson, ChatMessagePayload.class);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deserialize(Object) - Object를 타입 변환")
    void deserialize_Object_타입_변환() {
        ChatMessagePayload original = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(2L)
                .content("Hello")
                .build();

        String json = DataSerializer.serialize(original);
        Object rawMap = DataSerializer.deserialize(json, Object.class);

        ChatMessagePayload result = DataSerializer.deserialize(rawMap, ChatMessagePayload.class);

        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Hello");
    }
}
