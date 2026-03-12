package entity.gathering;

import entity.image.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringTest {

    private Gathering createGathering() {
        return Gathering.builder()
                .title("original title")
                .content("original content")
                .registerDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .count(0)
                .build();
    }

    @Test
    @DisplayName("change - 이미지가 null이 아닌 경우 이미지도 변경")
    void change_withImage() {
        Gathering gathering = createGathering();
        Image newImage = Image.builder().url("new.png").contentType("image/png").build();
        LocalDateTime before = LocalDateTime.now();

        gathering.change("new title", "new content", newImage);

        assertThat(gathering.getTitle()).isEqualTo("new title");
        assertThat(gathering.getContent()).isEqualTo("new content");
        assertThat(gathering.getGatheringImage()).isEqualTo(newImage);
        assertThat(gathering.getRegisterDate()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("change - 이미지가 null인 경우 기존 이미지 유지")
    void change_withNullImage() {
        Image originalImage = Image.builder().url("original.png").contentType("image/png").build();
        Gathering gathering = Gathering.builder()
                .title("title")
                .content("content")
                .registerDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .gatheringImage(originalImage)
                .build();

        gathering.change("new title", "new content", null);

        assertThat(gathering.getTitle()).isEqualTo("new title");
        assertThat(gathering.getContent()).isEqualTo("new content");
        assertThat(gathering.getGatheringImage()).isEqualTo(originalImage);
    }
}
