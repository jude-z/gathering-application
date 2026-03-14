package entity.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FailTest {

    @Test
    @DisplayName("of() 팩토리 메서드로 Fail 정상 생성")
    void of_정상_생성() {
        Fail fail = Fail.of("client-001", "error content", "test@test.com");

        assertThat(fail.getClientId()).isEqualTo("client-001");
        assertThat(fail.getContent()).isEqualTo("error content");
        assertThat(fail.getEmail()).isEqualTo("test@test.com");
    }
}
