package querydsl.repository.gathering;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.PageInfo;
import dto.PageableInfo;
import dto.querydsl.QueryDslPageResponse;
import entity.gathering.Gathering;
import entity.user.QUser;
import lombok.RequiredArgsConstructor;
import response.gathering.querydto.GatheringsQuery;
import response.gathering.querydto.ParticipatedQuery;
import util.PageCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static entity.category.QCategory.*;
import static entity.enrollment.QEnrollment.*;
import static entity.fcm.QFCMToken.*;
import static entity.fcm.QTopic.*;
import static entity.gathering.QGathering.*;
import static entity.image.QImage.*;
import static entity.like.QLike.*;
import static entity.recommend.QRecommend.*;
import static entity.user.QUser.*;

@RequiredArgsConstructor
public class QueryDslGatheringRepository {
    private final JPAQueryFactory queryFactory;

    // === gatheringParticipated ===

    public QueryDslPageResponse<ParticipatedQuery> gatheringParticipated(PageableInfo pageableInfo, Long gatheringId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser u = new QUser("u");

        List<ParticipatedQuery> content = queryFactory.select(Projections.constructor(ParticipatedQuery.class,
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
        PageInfo pageInfo = PageCalculator.of(offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === gatheringsCategory ===

    public QueryDslPageResponse<GatheringsQuery> gatheringsCategory(PageableInfo pageableInfo, String categoryName) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser cr = new QUser("cr");

        List<GatheringsQuery> content = queryFactory.select(Projections.constructor(GatheringsQuery.class,
                        gathering.id, gathering.title, gathering.content, gathering.registerDate,
                        category.name, cr.username, image.url, gathering.count))
                .from(gathering)
                .leftJoin(category).on(category.id.eq(gathering.category.id))
                .leftJoin(gathering.createBy, cr)
                .leftJoin(gathering.gatheringImage, image)
                .where(category.name.eq(categoryName))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(gathering.count())
                .from(gathering)
                .leftJoin(category).on(category.id.eq(gathering.category.id))
                .where(category.name.eq(categoryName))
                .fetchOne();
        int elementSize = content.size();
        PageInfo pageInfo = PageCalculator.of(offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === gatheringsLike ===

    public QueryDslPageResponse<GatheringsQuery> gatheringsLike(PageableInfo pageableInfo, Long userId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser cr = new QUser("cr");
        QUser u = new QUser("u");

        List<GatheringsQuery> content = queryFactory.select(Projections.constructor(GatheringsQuery.class,
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
                .fetch();

        Long totalCount = queryFactory.select(gathering.count())
                .from(gathering)
                .leftJoin(like).on(like.gathering.id.eq(gathering.id))
                .leftJoin(like.likedBy, u)
                .where(u.id.eq(userId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo pageInfo = PageCalculator.of(offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === gatheringsRecommend ===

    public List<GatheringsQuery> gatheringsRecommend(LocalDate localDate) {
        QUser cr = new QUser("cr");

        return queryFactory.select(Projections.constructor(GatheringsQuery.class,
                        gathering.id, gathering.title, gathering.content, gathering.registerDate,
                        category.name, cr.username, image.url, gathering.count))
                .from(recommend)
                .join(recommend.gathering, gathering)
                .join(category).on(category.id.eq(gathering.category.id))
                .join(gathering.createBy, cr)
                .leftJoin(gathering.gatheringImage, image)
                .where(recommend.date.eq(localDate))
                .orderBy(recommend.score.desc())
                .limit(10)
                .fetch();
    }

    // === findTopicById ===

    public Optional<Gathering> findTopicById(Long gatheringId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(gathering)
                        .leftJoin(gathering.topic, topic).fetchJoin()
                        .where(gathering.id.eq(gatheringId))
                        .fetchOne()
        );
    }

    // === findGatheringFetchCreatedByAndTokensId ===

    public Optional<Gathering> findGatheringFetchCreatedByAndTokensId(Long gatheringId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(gathering)
                        .leftJoin(gathering.createBy, user).fetchJoin()
                        .leftJoin(user.tokens, fCMToken).fetchJoin()
                        .where(gathering.id.eq(gatheringId))
                        .fetchOne()
        );
    }

    // === findGatheringFetchCreatedAndTopicBy ===

    public Optional<Gathering> findGatheringFetchCreatedAndTopicBy(Long gatheringId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(gathering)
                        .leftJoin(gathering.createBy, user).fetchJoin()
                        .leftJoin(gathering.topic, topic).fetchJoin()
                        .where(gathering.id.eq(gatheringId))
                        .fetchOne()
        );
    }
}
