package infra.repository.chat;

import entity.chat.ChatMessage;
import entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessageByChatRoom(ChatRoom chatRoom);
}
