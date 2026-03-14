package send.controller;

import infra.kafka.KafkaProducer;
import infra.kafka.event.Event;
import infra.kafka.event.EventType;
import infra.kafka.event.payload.ChatMessagePayload;
import infra.redis.generator.IdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendControllerTest {

    @InjectMocks
    private SendController sendController;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private IdGenerator idGenerator;

    @Test
    @DisplayName("sendMessage 호출 시 Kafka 이벤트 발행")
    void sendMessage_성공() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(1L)
                .content("hello")
                .createdAt(LocalDateTime.now())
                .build();
        given(idGenerator.nextIdForReply()).willReturn(100L);

        sendController.sendMessage(1L, payload);

        verify(kafkaProducer).publishEvent(any(Event.class));
    }

    @Test
    @DisplayName("createChatMessageEvent 이벤트 생성 확인")
    void createChatMessageEvent_확인() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L)
                .publisherId(1L)
                .content("hello")
                .createdAt(LocalDateTime.now())
                .build();
        given(idGenerator.nextIdForReply()).willReturn(100L);

        Event event = sendController.createChatMessageEvent(1L, payload);

        assertThat(event.getType()).isEqualTo(EventType.CHAT_MESSAGE);
        assertThat(event.getPayload()).isEqualTo(payload);
        assertThat(payload.getPartitionKey()).isEqualTo(1L);
        assertThat(payload.getEventId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("sendMessage 호출 시 partitionKey가 chatRoomId로 설정")
    void sendMessage_partitionKey_설정() {
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(5L)
                .publisherId(2L)
                .content("test message")
                .createdAt(LocalDateTime.now())
                .build();
        given(idGenerator.nextIdForReply()).willReturn(200L);

        sendController.sendMessage(5L, payload);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(kafkaProducer).publishEvent(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();
        ChatMessagePayload capturedPayload = (ChatMessagePayload) capturedEvent.getPayload();
        assertThat(capturedPayload.getPartitionKey()).isEqualTo(5L);
        assertThat(capturedPayload.getEventId()).isEqualTo(200L);
    }
}
