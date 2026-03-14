package reply.consumer;

import infra.kafka.event.Event;
import infra.kafka.event.EventPublisher;
import infra.kafka.event.payload.ChatMessageCompletePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import reply.service.ChatReplyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaReplyConsumerTest {

    @InjectMocks
    private KafkaReplyConsumer kafkaReplyConsumer;
    @Mock
    private ChatReplyService chatReplyService;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    @DisplayName("chatMessageListen 수신 시 handle 호출 후 완료 이벤트 발행 및 ACK")
    void chatMessageListen_성공() {
        String message = "{\"type\":\"CHAT_MESSAGE\",\"payload\":{\"chatRoomId\":1,\"publisherId\":1,\"content\":\"hello\",\"createdAt\":\"2025-01-01T12:00:00\"}}";
        ChatMessageCompletePayload completePayload = ChatMessageCompletePayload.builder()
                .chatRoomId(1L).publisherId(1L).content("hello").build();
        Event<ChatMessageCompletePayload> completeEvent = (Event<ChatMessageCompletePayload>) (Event) Event.builder()
                .type(infra.kafka.event.EventType.CHAT_MESSAGE_COMPLETE)
                .payload(completePayload)
                .build();
        given(eventPublisher.createCompleteEvent(any())).willReturn(completeEvent);

        kafkaReplyConsumer.chatMessageListen(message, acknowledgment);

        verify(chatReplyService).handle(any());
        verify(eventPublisher).createCompleteEvent(any());
        verify(eventPublisher).publishEvent(any());
        verify(acknowledgment).acknowledge();
    }
}
