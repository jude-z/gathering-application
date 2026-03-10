package infra.repository.chat;

import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    Optional<ChatParticipant> findByChatRoomAndUserAndStatus(ChatRoom chatRoom, User user, boolean status);
    List<ChatParticipant> findAllByChatRoomAndStatus(ChatRoom chatRoom, boolean status);
}
