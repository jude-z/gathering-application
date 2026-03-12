package reply.common.mapper;

import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ReadStatus;

public class ReadStatusMapper {
    public static ReadStatus toReadStatus(ChatParticipant chatParticipant, ChatMessage chatMessage){
        return ReadStatus.builder()
                .status(false)
                .chatMessage(chatMessage)
                .chatParticipant(chatParticipant)
                .build();
    }
}
