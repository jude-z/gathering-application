package querydsl.repository.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.PageInfo;
import dto.PageableInfo;
import dto.querydsl.QueryDslPageResponse;
import entity.image.Image;
import lombok.RequiredArgsConstructor;
import util.PageCalculator;

import java.util.List;
import java.util.Optional;

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
        PageInfo pageInfo = PageCalculator.of(offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    public Optional<Image> findByUrl(String imageUrl) {
        return Optional.ofNullable(
                queryFactory.selectFrom(image)
                        .where(image.url.eq(imageUrl))
                        .fetchOne()
        );
    }
}
