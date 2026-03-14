package send.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ChatSendServiceTest {

    @InjectMocks
    private ChatSendService chatSendService;

    @Test
    @DisplayName("isRoomParticipant 항상 true 반환")
    void isRoomParticipant_true반환() {
        boolean result = chatSendService.isRoomParticipant(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRoomParticipant null userId에도 true 반환")
    void isRoomParticipant_nullUserId() {
        boolean result = chatSendService.isRoomParticipant(null, 1L);

        assertThat(result).isTrue();
    }
}
