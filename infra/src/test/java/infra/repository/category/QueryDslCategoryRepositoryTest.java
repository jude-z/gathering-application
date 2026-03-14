package infra.repository.category;

import entity.category.Category;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslCategoryRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslCategoryRepository queryDslCategoryRepository;

    @Test
    @DisplayName("findBy - 모임의 카테고리 조회")
    void findBy_카테고리_조회() {
        Optional<Category> result = queryDslCategoryRepository.findBy(gathering1.getId(), "category1");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("category1");
    }

    @Test
    @DisplayName("findBy - 존재하지 않는 카테고리 조회 시 빈 결과")
    void findBy_존재하지_않는_카테고리() {
        Optional<Category> result = queryDslCategoryRepository.findBy(gathering1.getId(), "unknown");

        assertThat(result).isEmpty();
    }
}
