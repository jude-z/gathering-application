package entity.certification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationTest {

    @Test
    @DisplayName("Builder로 Certification 정상 생성")
    void builder_정상_생성() {
        Certification certification = Certification.builder()
                .email("test@test.com")
                .certification("ABC123")
                .build();

        assertThat(certification.getEmail()).isEqualTo("test@test.com");
        assertThat(certification.getCertification()).isEqualTo("ABC123");
    }
}
