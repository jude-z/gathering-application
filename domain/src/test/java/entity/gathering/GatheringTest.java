package entity.gathering;

import entity.image.Image;
import entity.user.Role;
import entity.user.User;
import entity.category.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringTest {

    @Test
    @DisplayName("Builder로 Gathering 정상 생성 - enrollments 빈 리스트 초기화")
    void builder_정상_생성() {
        User user = User.builder().username("creator").password("pw").role(Role.USER).nickname("nick").build();
        Category category = Category.builder().name("category1").build();
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        Gathering gathering = Gathering.builder()
                .title("Test Gathering")
                .content("Content")
                .registerDate(now)
                .createBy(user)
                .count(10)
                .category(category)
                .build();

        assertThat(gathering.getTitle()).isEqualTo("Test Gathering");
        assertThat(gathering.getContent()).isEqualTo("Content");
        assertThat(gathering.getRegisterDate()).isEqualTo(now);
        assertThat(gathering.getCreateBy()).isEqualTo(user);
        assertThat(gathering.getCount()).isEqualTo(10);
        assertThat(gathering.getCategory()).isEqualTo(category);
        assertThat(gathering.getEnrollments()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("change() 호출 시 제목, 내용, 이미지, 등록일 변경")
    void change_정보_변경() {
        Gathering gathering = Gathering.builder()
                .title("Old Title")
                .content("Old Content")
                .registerDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .count(5)
                .build();

        Image newImage = Image.builder().url("new-url").contentType("image/png").build();
        LocalDateTime before = LocalDateTime.now();

        gathering.change("New Title", "New Content", newImage);

        assertThat(gathering.getTitle()).isEqualTo("New Title");
        assertThat(gathering.getContent()).isEqualTo("New Content");
        assertThat(gathering.getGatheringImage()).isEqualTo(newImage);
        assertThat(gathering.getRegisterDate()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("change() 호출 시 이미지가 null이면 기존 이미지 유지")
    void change_이미지_null이면_기존_유지() {
        Image originalImage = Image.builder().url("original-url").contentType("image/jpeg").build();
        Gathering gathering = Gathering.builder()
                .title("Title")
                .content("Content")
                .gatheringImage(originalImage)
                .registerDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        gathering.change("New Title", "New Content", null);

        assertThat(gathering.getGatheringImage()).isEqualTo(originalImage);
    }
}
