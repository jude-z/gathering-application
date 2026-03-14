package infra.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import page.PageInfo;
import page.PageableInfo;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import page.PageCalculator;

import java.util.List;
import java.util.Optional;

import static entity.user.QUser.*;
import static entity.image.QImage.*;
import static entity.gathering.QGathering.*;
import static entity.enrollment.QEnrollment.*;

@Repository
@RequiredArgsConstructor
public class QueryDslUserRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<User> findByEmail(PageableInfo pageableInfo, String email) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        List<User> content = queryFactory.selectFrom(user)
                .where(user.email.eq(email))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(user.count())
                .from(user)
                .where(user.email.eq(email))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<User>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    public Optional<User> findByIdFetchImage(Long userId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .leftJoin(user.profileImage, image).fetchJoin()
                        .where(user.id.eq(userId))
                        .fetchOne()
        );
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(user.username.eq(username))
                        .fetchOne()
        );
    }

    public QueryDslPageResponse<User> findEnrollmentById(PageableInfo pageableInfo, Long gatheringId, Long userId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        List<User> content = queryFactory.select(enrollment.enrolledBy)
                .from(gathering)
                .join(gathering.enrollments, enrollment)
                .join(enrollment.enrolledBy, user)
                .where(
                        gathering.id.eq(gatheringId),
                        user.id.ne(userId))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(enrollment.count())
                .from(gathering)
                .join(gathering.enrollments, enrollment)
                .join(enrollment.enrolledBy, user)
                .where(
                        gathering.id.eq(gatheringId),
                        user.id.ne(userId))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<User>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    public QueryDslPageResponse<User> findEnrollmentById( Long gatheringId, Long userId) {

        List<User> content = queryFactory.select(enrollment.enrolledBy)
                .from(gathering)
                .join(gathering.enrollments, enrollment)
                .join(enrollment.enrolledBy, user)
                .where(
                        gathering.id.eq(gatheringId),
                        user.id.ne(userId))
                .fetch();

        Long totalCount = queryFactory.select(enrollment.count())
                .from(gathering)
                .join(gathering.enrollments, enrollment)
                .join(enrollment.enrolledBy, user)
                .where(
                        gathering.id.eq(gatheringId),
                        user.id.ne(userId))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<User>> pageInfo = PageCalculator.toPageInfo(content, -1, -1, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }
}
