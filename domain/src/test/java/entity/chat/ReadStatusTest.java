package entity.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReadStatusTest {

    @Test
    @DisplayName("Builder로 ReadStatus 정상 생성")
    void builder_정상_생성() {
        ChatParticipant participant = ChatParticipant.builder().status(true).build();
        ChatMessage message = ChatMessage.builder().id(1L).content("msg").build();

        ReadStatus readStatus = ReadStatus.builder()
                .status(true)
                .chatParticipant(participant)
                .chatMessage(message)
                .build();

        assertThat(readStatus.getStatus()).isTrue();
        assertThat(readStatus.getChatParticipant()).isEqualTo(participant);
        assertThat(readStatus.getChatMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("ReadStatus의 status가 null일 수 있음 (Boolean 타입)")
    void status_null_허용() {
        ReadStatus readStatus = ReadStatus.builder()
                .status(null)
                .build();

        assertThat(readStatus.getStatus()).isNull();
    }
}
