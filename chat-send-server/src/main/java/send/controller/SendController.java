package send.controller;


import infra.kafka.KafkaProducer;
import infra.kafka.event.Event;
import infra.kafka.event.EventPayload;
import infra.kafka.event.EventType;
import infra.kafka.event.payload.ChatMessageCompletePayload;
import infra.kafka.event.payload.ChatMessagePayload;
import infra.redis.generator.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SendController {

    private final KafkaProducer kafkaProducer;
    private final IdGenerator idGenerator;

    @MessageMapping("/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, @Payload ChatMessagePayload chatMessagePayload){
        Event chatMessageEvent = createChatMessageEvent(chatRoomId, chatMessagePayload);
        kafkaProducer.publishEvent(chatMessageEvent);
    }

    public Event createChatMessageEvent(Long chatRoomId, ChatMessagePayload chatMessagePayload) {
        Long chatSendId = idGenerator.nextIdForReply();

        chatMessagePayload.setPartitionKey(chatRoomId);
        chatMessagePayload.setEventId(chatSendId);
        return Event.builder()
                .payload(chatMessagePayload)
                .type(EventType.CHAT_MESSAGE)
                .build();
    }
}
