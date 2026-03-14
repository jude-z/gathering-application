package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageUrlConverterTest {

    @Test
    @DisplayName("convert - path와 rawUrl을 결합하여 반환")
    void convert_경로_결합() {
        ImageUrlConverter converter = new ImageUrlConverter("https://cdn.example.com/");

        String result = converter.convert("images/photo.jpg");

        assertThat(result).isEqualTo("https://cdn.example.com/images/photo.jpg");
    }
}
