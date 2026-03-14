package infra.repository.chat;

import entity.chat.ReadStatus;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcChatRepositoryTest extends RepositoryTestSupport {

    @Autowired
    JdbcChatRepository jdbcChatRepository;

    @Test
    @DisplayName("readChatMessage - 메시지 읽음 처리")
    void readChatMessage_읽음_처리() {
        jdbcChatRepository.readChatMessage(
                chatParticipant2.getId(),
                List.of(chatMessage1.getId()));

        em.clear();
        ReadStatus updated = em.find(ReadStatus.class, readStatus1.getId());
        assertThat(updated.getStatus()).isTrue();
    }

    @Test
    @DisplayName("readChatMessage - 빈 메시지 리스트는 무시")
    void readChatMessage_빈_리스트() {
        jdbcChatRepository.readChatMessage(chatParticipant2.getId(), List.of());

        em.clear();
        ReadStatus unchanged = em.find(ReadStatus.class, readStatus1.getId());
        assertThat(unchanged.getStatus()).isFalse();
    }
}
