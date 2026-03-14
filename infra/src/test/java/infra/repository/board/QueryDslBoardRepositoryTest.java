package infra.repository.board;

import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.board.BoardProjection;
import infra.repository.dto.querydsl.board.BoardsProjection;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslBoardRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslBoardRepository queryDslBoardRepository;

    @Disabled("fetchBoard 쿼리에서 QImage 별칭이 profileImage/boardImage 조인에 중복 사용되어 AliasCollisionException 발생 - 프로덕션 쿼리 수정 필요")
    @Test
    @DisplayName("fetchBoard - 게시글 상세 조회")
    void fetchBoard_상세_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<BoardProjection> result =
                queryDslBoardRepository.fetchBoard(pageableInfo, board1.getId());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Board 1");
    }

    @Test
    @DisplayName("fetchBoards - 게시글 목록 조회")
    void fetchBoards_목록_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<BoardsProjection> result =
                queryDslBoardRepository.fetchBoards(pageableInfo);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalCount()).isGreaterThanOrEqualTo(1);
    }
}
