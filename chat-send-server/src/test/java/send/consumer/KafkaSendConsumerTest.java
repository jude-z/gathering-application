package send.consumer;

import infra.kafka.event.payload.ChatMessageCompletePayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaSendConsumerTest {

    @InjectMocks
    private KafkaSendConsumer kafkaSendConsumer;
    @Mock
    private SimpMessageSendingOperations messageSendingOperations;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    @DisplayName("chatMessageListen 메시지 수신 후 WebSocket 전송 및 ACK")
    void chatMessageListen_성공() {
        String message = "{\"type\":\"CHAT_MESSAGE_COMPLETE\",\"payload\":{\"chatRoomId\":1,\"publisherId\":1,\"content\":\"hello\",\"createdAt\":\"2025-01-01T12:00:00\"}}";

        kafkaSendConsumer.chatMessageListen(message, acknowledgment);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageSendingOperations).convertAndSend(destinationCaptor.capture(), org.mockito.ArgumentMatchers.any(ChatMessageCompletePayload.class));
        assertThat(destinationCaptor.getValue()).isEqualTo("/chatRoom/1");
        verify(acknowledgment).acknowledge();
    }
}
