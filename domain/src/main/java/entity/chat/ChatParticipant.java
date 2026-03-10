package entity.chat;

import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "chat_participant")
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    private boolean status;

    public void changeStatus(boolean status) {
        this.status = status;
    }

    @Builder
    private ChatParticipant(User user, ChatRoom chatRoom, boolean status) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.status = status;
    }

    public static ChatParticipant of(ChatRoom chatRoom, User user, boolean status){
        return ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .status(status)
                .build();
    }


}
