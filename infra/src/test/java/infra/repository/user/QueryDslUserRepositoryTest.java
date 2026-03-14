package infra.repository.user;

import entity.user.User;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslUserRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslUserRepository queryDslUserRepository;

    @Test
    @DisplayName("findByEmail - 존재하는 이메일로 유저 조회")
    void findByEmail_존재하는_이메일() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<User> result = queryDslUserRepository.findByEmail(pageableInfo, "user1@test.com");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("user1@test.com");
        assertThat(result.getTotalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByEmail - 존재하지 않는 이메일 조회 시 빈 결과")
    void findByEmail_존재하지_않는_이메일() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<User> result = queryDslUserRepository.findByEmail(pageableInfo, "unknown@test.com");

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalCount()).isZero();
    }

    @Test
    @DisplayName("findByIdFetchImage - 유저와 프로필 이미지 함께 조회")
    void findByIdFetchImage_프로필_이미지_함께_조회() {
        Optional<User> result = queryDslUserRepository.findByIdFetchImage(user1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        assertThat(result.get().getProfileImage()).isNotNull();
        assertThat(result.get().getProfileImage().getUrl()).isEqualTo("profile1.jpg");
    }

    @Test
    @DisplayName("findByUsername - 존재하는 유저네임으로 조회")
    void findByUsername_존재하는_유저() {
        Optional<User> result = queryDslUserRepository.findByUsername("user1");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    @DisplayName("findEnrollmentById - 모임에 가입된 유저 조회 (본인 제외)")
    void findEnrollmentById_가입된_유저_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<User> result = queryDslUserRepository.findEnrollmentById(
                pageableInfo, gathering1.getId(), user1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("user2");
    }
}
