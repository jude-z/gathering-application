package infra.kafka.event;

import infra.kafka.event.payload.ChatMessageCompletePayload;
import infra.kafka.event.payload.ChatMessagePayload;
import infra.redis.generator.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final IdGenerator idGenerator;

    public void publishEvent(Event<? extends EventPayload> event){
        eventPublisher.publishEvent(event);
    }

    public Event createCompleteEvent(Event<ChatMessagePayload> event) {
        Long chatReplyId = idGenerator.nextIdForReply();

        ChatMessagePayload payload = event.getPayload();

        ChatMessageCompletePayload completePayload = ChatMessageCompletePayload.builder()
                .chatRoomId(payload.getChatRoomId())
                .publisherId(payload.getPublisherId())
                .content(payload.getContent())
                .createdAt(payload.getCreatedAt())
                .build();
        completePayload.setPartitionKey(payload.getPartitionKey());
        completePayload.setEventId(chatReplyId);

        return Event.of(EventType.CHAT_MESSAGE_COMPLETE, completePayload);
    }
}
