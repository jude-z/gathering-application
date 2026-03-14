package infra.repository.like;

import entity.like.Like;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslLikeRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslLikeRepository queryDslLikeRepository;

    @Test
    @DisplayName("findLike - 좋아요 조회")
    void findLike_존재하는_좋아요() {
        Optional<Like> result = queryDslLikeRepository.findLike(user1.getId(), gathering1.getId());

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("findLike - 좋아요하지 않은 경우 빈 결과")
    void findLike_존재하지_않는_좋아요() {
        Optional<Like> result = queryDslLikeRepository.findLike(user2.getId(), gathering1.getId());

        assertThat(result).isEmpty();
    }
}
