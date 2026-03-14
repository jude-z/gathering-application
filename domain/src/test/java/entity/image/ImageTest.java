package entity.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    @Test
    @DisplayName("Builder로 Image 정상 생성")
    void builder_정상_생성() {
        Image image = Image.builder()
                .url("https://example.com/image.jpg")
                .contentType("image/jpeg")
                .build();

        assertThat(image.getUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(image.getContentType()).isEqualTo("image/jpeg");
        assertThat(image.getBoard()).isNull();
        assertThat(image.getGathering()).isNull();
    }
}
