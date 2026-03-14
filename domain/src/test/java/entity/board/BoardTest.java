package entity.board;

import entity.user.Role;
import entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    @Test
    @DisplayName("Builder로 Board 정상 생성 - images 빈 리스트 초기화")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        Board board = Board.builder()
                .user(user)
                .title("Board Title")
                .description("Board Description")
                .registerDate(now)
                .build();

        assertThat(board.getUser()).isEqualTo(user);
        assertThat(board.getTitle()).isEqualTo("Board Title");
        assertThat(board.getDescription()).isEqualTo("Board Description");
        assertThat(board.getRegisterDate()).isEqualTo(now);
        assertThat(board.getImages()).isNotNull().isEmpty();
    }
}
