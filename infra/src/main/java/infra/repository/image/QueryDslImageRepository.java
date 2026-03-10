package infra.repository.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import infra.dto.PageInfo;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import lombok.RequiredArgsConstructor;
import util.PageCalculator;

import java.util.List;

import static entity.image.QImage.*;
import static entity.gathering.QGathering.*;

@RequiredArgsConstructor
public class QueryDslImageRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<String> gatheringImage(Long gatheringId, PageableInfo pageableInfo) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();
        List<String> content = queryFactory.select(image.url)
                .from(image)
                .join(image.gathering, gathering)
                .where(gathering.id.eq(gatheringId))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(image.count())
                .from(image)
                .join(image.gathering, gathering)
                .where(gathering.id.eq(gatheringId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<String>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }
}
