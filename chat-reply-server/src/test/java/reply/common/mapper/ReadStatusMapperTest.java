package reply.common.mapper;

import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.chat.ReadStatus;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReadStatusMapperTest {

    @Test
    @DisplayName("toReadStatus 정상 매핑")
    void toReadStatus_정상() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        ChatParticipant participant = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();
        ChatMessage chatMessage = ChatMessage.builder().chatRoom(chatRoom).chatParticipant(participant).build();

        ReadStatus readStatus = ReadStatusMapper.toReadStatus(participant, chatMessage);

        assertThat(readStatus).isNotNull();
        assertThat(readStatus.getStatus()).isFalse();
        assertThat(readStatus.getChatMessage()).isEqualTo(chatMessage);
        assertThat(readStatus.getChatParticipant()).isEqualTo(participant);
    }

    @Test
    @DisplayName("toReadStatus 상태가 false로 초기화")
    void toReadStatus_상태초기화() {
        ChatParticipant participant = ChatParticipant.builder().status(true).build();
        ChatMessage chatMessage = ChatMessage.builder().build();

        ReadStatus readStatus = ReadStatusMapper.toReadStatus(participant, chatMessage);

        assertThat(readStatus.getStatus()).isFalse();
    }
}
