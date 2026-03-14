package entity.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    @DisplayName("Builder로 ChatMessage 정상 생성 - 수동 ID 할당")
    void builder_정상_생성() {
        ChatRoom chatRoom = ChatRoom.builder().title("Room").description("Desc").count(1).build();
        ChatParticipant participant = ChatParticipant.builder().status(true).build();
        LocalDateTime createdAt = LocalDateTime.of(2025, 3, 1, 12, 0);

        ChatMessage message = ChatMessage.builder()
                .id(100L)
                .content("Hello")
                .chatRoom(chatRoom)
                .chatParticipant(participant)
                .createdAt(createdAt)
                .build();

        assertThat(message.getId()).isEqualTo(100L);
        assertThat(message.getContent()).isEqualTo("Hello");
        assertThat(message.getChatRoom()).isEqualTo(chatRoom);
        assertThat(message.getChatParticipant()).isEqualTo(participant);
        assertThat(message.getCreatedAt()).isEqualTo(createdAt);
    }
}
