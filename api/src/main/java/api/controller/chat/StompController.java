package api.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import spring.myproject.rabbitmq.publisher.ChatPublisher;

import static api.requeset.chat.ChatRequestDto.*;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final ChatPublisher chatPublisher;

    @MessageMapping("/chatRoom/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, @Payload ChatMessageRequest chatMessageRequest){
        chatPublisher.publish(chatRoomId, chatMessageRequest);
    }
}
