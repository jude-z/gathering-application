package entity.recommend;

import entity.gathering.Gathering;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendTest {

    @Test
    @DisplayName("Builder로 Recommend 정상 생성")
    void builder_정상_생성() {
        Gathering gathering = Gathering.builder().title("Gathering").content("Content").count(5).build();
        LocalDate today = LocalDate.of(2025, 3, 1);

        Recommend recommend = Recommend.builder()
                .gathering(gathering)
                .date(today)
                .build();

        assertThat(recommend.getGathering()).isEqualTo(gathering);
        assertThat(recommend.getDate()).isEqualTo(today);
    }
}
