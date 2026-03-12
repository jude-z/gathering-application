package reply.consumer;

import infra.kafka.event.Event;
import infra.kafka.event.EventPublisher;
import infra.kafka.event.EventType;
import infra.kafka.event.payload.ChatMessageCompletePayload;
import infra.kafka.event.payload.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import reply.service.ChatReplyService;

@RequiredArgsConstructor
@Slf4j
public class KafkaReplyConsumer {

    private final ChatReplyService chatReplyService;
    private final EventPublisher eventPublisher;

    @KafkaListener(topics = {
            EventType.Topic.CHAT_MESSAGE,
    },
            groupId = "#{T(java.util.UUID).randomUUID().toString()}"
    )
    public void chatMessageListen(String message, Acknowledgment ack) {
        Event<ChatMessagePayload> event = (Event<ChatMessagePayload>)(Event .fromJson(message));
        chatReplyService.handle(event);
        Event<ChatMessageCompletePayload> completeEvent = (Event<ChatMessageCompletePayload>)(eventPublisher.createCompleteEvent(event));
        eventPublisher.publishEvent(completeEvent);
        ack.acknowledge();
    }

}

