package entity.like;

import entity.user.Role;
import entity.user.User;
import entity.gathering.Gathering;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    @Test
    @DisplayName("Builder로 Like 정상 생성")
    void builder_정상_생성() {
        User user = User.builder().username("user").password("pw").role(Role.USER).nickname("nick").build();
        Gathering gathering = Gathering.builder().title("Gathering").content("Content").count(5).build();

        Like like = Like.builder()
                .likedBy(user)
                .gathering(gathering)
                .build();

        assertThat(like.getLikedBy()).isEqualTo(user);
        assertThat(like.getGathering()).isEqualTo(gathering);
    }
}
