package querydsl.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.PageableInfo;
import dto.querydsl.QueryDslPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import response.board.querydto.BoardQuery;
import response.board.querydto.BoardsQuery;

@RequiredArgsConstructor
public class QueryDslBoardRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<BoardQuery> fetchBoard(PageableInfo pageableInfo, Long boardId){

    }

    public Page<BoardsQuery> fetchBoards(PageableInfo pageableInfo){

    }

}
