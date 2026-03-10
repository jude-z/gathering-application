package entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "read_status")
public class ReadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_participant_id")
    private ChatParticipant chatParticipant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    @Builder
    public ReadStatus(Boolean status, ChatParticipant chatParticipant, ChatMessage chatMessage) {
        this.status = status;
        this.chatParticipant = chatParticipant;
        this.chatMessage = chatMessage;
    }
}
