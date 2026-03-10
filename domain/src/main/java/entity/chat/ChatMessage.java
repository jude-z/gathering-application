package entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_participant_id")
    private ChatParticipant chatParticipant;
    private LocalDateTime createdAt;

    @Builder
    private ChatMessage(String content, ChatRoom chatRoom, ChatParticipant chatParticipant, LocalDateTime createdAt) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.chatParticipant = chatParticipant;
        this.createdAt = createdAt;
    }

//    public static ChatMessage of(ChatRoom chatRoom, ChatParticipant chatParticipant, ChatRequestDto.ChatMessageRequest chatMessageRequest){
//        return ChatMessage.builder()
//                .chatRoom(chatRoom)
//                .chatParticipant(chatParticipant)
//                .content(chatMessageRequest.getContent())
//                .build();
//    }

}
