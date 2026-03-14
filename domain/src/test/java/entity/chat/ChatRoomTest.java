package entity.chat;

import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomTest {

    @Test
    @DisplayName("Builder로 ChatRoom 정상 생성")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();

        ChatRoom chatRoom = ChatRoom.builder()
                .title("Chat Room")
                .description("Description")
                .createdBy(user)
                .count(3)
                .build();

        assertThat(chatRoom.getTitle()).isEqualTo("Chat Room");
        assertThat(chatRoom.getDescription()).isEqualTo("Description");
        assertThat(chatRoom.getCreatedBy()).isEqualTo(user);
        assertThat(chatRoom.getCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("changeCount() 호출 시 참여자 수 변경")
    void changeCount_참여자_수_변경() {
        ChatRoom chatRoom = ChatRoom.builder()
                .title("Chat Room")
                .description("Description")
                .count(3)
                .build();

        chatRoom.changeCount(5);

        assertThat(chatRoom.getCount()).isEqualTo(5);
    }
}
