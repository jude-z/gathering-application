package infra.repository.board;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import page.PageInfo;
import page.PageableInfo;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.board.BoardProjection;
import infra.repository.dto.querydsl.board.BoardsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import page.PageCalculator;

import java.util.List;

import static entity.board.QBoard.*;
import static entity.image.QImage.*;
import static entity.user.QUser.*;

@Repository
@RequiredArgsConstructor
public class QueryDslBoardRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<BoardProjection> fetchBoard(PageableInfo pageableInfo, Long boardId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        List<BoardProjection> content = queryFactory
                .select(Projections.constructor(BoardProjection.class,
                        board.title,
                        board.description,
                        image.url,
                        user.username,
                        user.nickname,
                        image.url,
                        board.registerDate
                ))
                .from(board)
                .join(board.user, user)
                .leftJoin(user.profileImage, image)
                .leftJoin(image).on(image.board.eq(board))
                .where(board.id.eq(boardId))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory
                .select(image.count())
                .from(image)
                .where(image.board.id.eq(boardId))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<BoardProjection>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    public QueryDslPageResponse<BoardsProjection> fetchBoards(PageableInfo pageableInfo) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        List<BoardsProjection> content = queryFactory
                .select(Projections.constructor(BoardsProjection.class,
                        board.id,
                        board.title,
                        board.description,
                        user.nickname,
                        board.registerDate,
                        image.url
                ))
                .from(board)
                .join(board.user, user)
                .leftJoin(user.profileImage, image)
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<BoardsProjection>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }
}
