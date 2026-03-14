package infra.repository.gathering;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import page.PageInfo;
import page.PageableInfo;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.gathering.GatheringsProjection;
import infra.repository.dto.querydsl.gathering.ParticipatedProjection;
import entity.gathering.Gathering;
import entity.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import page.PageCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static entity.category.QCategory.*;
import static entity.enrollment.QEnrollment.*;
import static entity.gathering.QGathering.*;
import static entity.image.QImage.*;
import static entity.like.QLike.*;
import static entity.recommend.QRecommend.*;
import static entity.user.QUser.*;

@Repository
@RequiredArgsConstructor
public class QueryDslGatheringRepository {
    private final JPAQueryFactory queryFactory;

    // === gatheringParticipated ===

    public QueryDslPageResponse<ParticipatedProjection> gatheringParticipated(PageableInfo pageableInfo, Long gatheringId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser u = new QUser("u");

        List<ParticipatedProjection> content = queryFactory.select(Projections.constructor(ParticipatedProjection.class,
                        u.username, u.nickname, image.url))
                .from(gathering)
                .leftJoin(gathering.enrollments, enrollment)
                .leftJoin(enrollment.enrolledBy, u)
                .leftJoin(u.profileImage, image)
                .where(gathering.id.eq(gatheringId))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(enrollment.count())
                .from(gathering)
                .leftJoin(gathering.enrollments, enrollment)
                .where(gathering.id.eq(gatheringId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<ParticipatedProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === gatheringsCategory ===

    public QueryDslPageResponse<GatheringsProjection> gatheringsCategory(PageableInfo pageableInfo, String categoryName) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser cr = new QUser("cr");

        List<GatheringsProjection> content = queryFactory.select(Projections.constructor(GatheringsProjection.class,
                        gathering.id, gathering.title, gathering.content, gathering.registerDate,
                        category.name, cr.username, image.url, gathering.count))
                .from(gathering)
                .leftJoin(category).on(category.id.eq(gathering.category.id))
                .leftJoin(gathering.createBy, cr)
                .leftJoin(gathering.gatheringImage, image)
                .where(category.name.eq(categoryName))
                .offset(offset)
                .limit(limit)
                .orderBy(gathering.id.desc())
                .fetch();

        Long totalCount = queryFactory.select(gathering.count())
                .from(gathering)
                .leftJoin(category).on(category.id.eq(gathering.category.id))
                .where(category.name.eq(categoryName))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<GatheringsProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === gatheringsLike ===

    public QueryDslPageResponse<GatheringsProjection> gatheringsLike(PageableInfo pageableInfo, Long userId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser cr = new QUser("cr");
        QUser u = new QUser("u");

        List<GatheringsProjection> content = queryFactory.select(Projections.constructor(GatheringsProjection.class,
                        gathering.id, gathering.title, gathering.content, gathering.registerDate,
                        category.name, cr.username, image.url, gathering.count))
                .from(gathering)
                .leftJoin(gathering.gatheringImage, image)
                .leftJoin(category).on(category.id.eq(gathering.category.id))
                .leftJoin(gathering.createBy, cr)
                .leftJoin(like).on(like.gathering.id.eq(gathering.id))
                .leftJoin(like.likedBy, u)
                .where(u.id.eq(userId))
                .offset(offset)
                .limit(limit)
                .orderBy(gathering.id.desc())
                .fetch();

        Long totalCount = queryFactory.select(gathering.count())
                .from(gathering)
                .leftJoin(like).on(like.gathering.id.eq(gathering.id))
                .leftJoin(like.likedBy, u)
                .where(u.id.eq(userId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<GatheringsProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === gatheringsRecommend ===

    public QueryDslPageResponse<GatheringsProjection> gatheringsRecommend(PageableInfo pageableInfo, LocalDate localDate) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser cr = new QUser("cr");

        List<GatheringsProjection> content = queryFactory.select(Projections.constructor(GatheringsProjection.class,
                        gathering.id, gathering.title, gathering.content, gathering.registerDate,
                        category.name, cr.username, image.url, gathering.count))
                .from(recommend)
                .join(recommend.gathering, gathering)
                .join(category).on(category.id.eq(gathering.category.id))
                .join(gathering.createBy, cr)
                .leftJoin(gathering.gatheringImage, image)
                .where(recommend.date.eq(localDate))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(recommend.count())
                .from(recommend)
                .where(recommend.date.eq(localDate))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<GatheringsProjection>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === findGatheringFetchCreatedBy ===

    public Optional<Gathering> findGatheringFetchCreatedBy(Long gatheringId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(gathering)
                        .leftJoin(gathering.createBy, user).fetchJoin()
                        .where(gathering.id.eq(gatheringId))
                        .fetchOne()
        );
    }
}
