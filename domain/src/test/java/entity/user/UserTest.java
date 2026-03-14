package entity.user;

import entity.image.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("Builder로 User 정상 생성")
    void builder_정상_생성() {
        User user = User.builder()
                .username("testUser")
                .password("password123")
                .email("test@test.com")
                .address("Seoul")
                .age(25)
                .hobby("coding")
                .role(Role.USER)
                .nickname("tester")
                .refreshToken("token123")
                .build();

        assertThat(user.getUsername()).isEqualTo("testUser");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getAddress()).isEqualTo("Seoul");
        assertThat(user.getAge()).isEqualTo(25);
        assertThat(user.getHobby()).isEqualTo("coding");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getRefreshToken()).isEqualTo("token123");
        assertThat(user.getProfileImage()).isNull();
    }

    @Test
    @DisplayName("change() 호출 시 사용자 정보 변경")
    void change_사용자_정보_변경() {
        User user = User.builder()
                .username("testUser")
                .password("oldPassword")
                .email("old@test.com")
                .address("Seoul")
                .age(25)
                .hobby("coding")
                .role(Role.USER)
                .nickname("oldNick")
                .build();

        user.change("newPassword", "new@test.com", "Busan", 30, "reading", "newNick");

        assertThat(user.getPassword()).isEqualTo("newPassword");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getAddress()).isEqualTo("Busan");
        assertThat(user.getAge()).isEqualTo(30);
        assertThat(user.getHobby()).isEqualTo("reading");
        assertThat(user.getNickname()).isEqualTo("newNick");
    }

    @Test
    @DisplayName("changeProfileImage() 호출 시 프로필 이미지 변경")
    void changeProfileImage_프로필_이미지_변경() {
        User user = User.builder()
                .username("testUser")
                .password("password")
                .email("test@test.com")
                .role(Role.USER)
                .nickname("tester")
                .build();

        Image image = Image.builder()
                .url("https://example.com/image.jpg")
                .contentType("image/jpeg")
                .build();

        user.changeProfileImage(image);

        assertThat(user.getProfileImage()).isEqualTo(image);
    }
}
