package entity.chat;

import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatParticipantTest {

    @Test
    @DisplayName("Builder로 ChatParticipant 정상 생성")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();
        ChatRoom chatRoom = ChatRoom.builder().title("Room").description("Desc").count(1).build();

        ChatParticipant participant = ChatParticipant.builder()
                .user(user)
                .chatRoom(chatRoom)
                .status(true)
                .build();

        assertThat(participant.getUser()).isEqualTo(user);
        assertThat(participant.getChatRoom()).isEqualTo(chatRoom);
        assertThat(participant.isStatus()).isTrue();
    }

    @Test
    @DisplayName("changeStatus() 호출 시 상태 변경")
    void changeStatus_상태_변경() {
        ChatParticipant participant = ChatParticipant.builder()
                .status(true)
                .build();

        participant.changeStatus(false);

        assertThat(participant.isStatus()).isFalse();
    }
}
