package send.consumer;

import infra.kafka.event.Event;
import infra.kafka.event.EventType;
import infra.kafka.event.payload.ChatMessageCompletePayload;
import infra.kafka.event.payload.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSendConsumer {
    private final SimpMessageSendingOperations messageSendingOperations;

    @KafkaListener(topics = {
            EventType.Topic.CHAT_MESSAGE_COMPLETE,
    },
            groupId = "#{T(java.util.UUID).randomUUID().toString()}"
    )
    public void chatMessageListen(String message, Acknowledgment ack) {
        Event<ChatMessageCompletePayload> event = (Event<ChatMessageCompletePayload>)(Event .fromJson(message));
        ChatMessageCompletePayload payload = event.getPayload();
        Long chatRoomId = payload.getChatRoomId();
        messageSendingOperations.convertAndSend("/chatRoom/" + chatRoomId,payload);
        ack.acknowledge();
    }
}
