package querydsl.repository.fcm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.PageInfo;
import dto.PageableInfo;
import dto.querydsl.QueryDslPageResponse;
import entity.fcm.FCMToken;
import entity.fcm.UserTopic;
import lombok.RequiredArgsConstructor;
import util.PageCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static entity.fcm.QFCMToken.*;
import static entity.fcm.QTopic.*;
import static entity.fcm.QUserTopic.*;
import static entity.user.QUser.*;

@RequiredArgsConstructor
public class QueryDslFcmRepository {
    private final JPAQueryFactory queryFactory;

    // === FCMTokenRepository: findByTokenValueAndUser ===

    public Optional<FCMToken> findByTokenValueAndUser(String tokenValue, Long userId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(fCMToken)
                        .join(fCMToken.user, user)
                        .where(
                                fCMToken.tokenValue.eq(tokenValue),
                                user.id.eq(userId))
                        .fetchOne()
        );
    }

    // === FCMTokenRepository: findByExpirationDate ===

    public QueryDslPageResponse<FCMToken> findByExpirationDate(PageableInfo pageableInfo, LocalDate now) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        List<FCMToken> content = queryFactory.selectFrom(fCMToken)
                .where(fCMToken.expirationDate.gt(now))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(fCMToken.count())
                .from(fCMToken)
                .where(fCMToken.expirationDate.gt(now))
                .fetchOne();
        int elementSize = content.size();
        PageInfo pageInfo = PageCalculator.of(offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === UserTopicRepository: findByUserId ===

    public List<UserTopic> findByUserId(Long userId) {
        return queryFactory.selectFrom(userTopic)
                .leftJoin(userTopic.topic, topic).fetchJoin()
                .leftJoin(userTopic.user, user)
                .where(user.id.eq(userId))
                .fetch();
    }

    // === UserTopicRepository: existsByTopicAndUser ===

    public boolean existsByTopicAndUser(String topicName, Long userId) {
        return queryFactory.select(userTopic.count())
                .from(userTopic)
                .join(userTopic.topic, topic)
                .where(
                        topic.topicName.eq(topicName),
                        userTopic.user.id.eq(userId))
                .fetchOne() > 0;
    }
}
