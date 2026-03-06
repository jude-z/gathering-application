package querydsl.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.user.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static entity.user.QUser.*;
import static entity.image.QImage.*;
import static entity.fcm.QFCMToken.*;
import static entity.gathering.QGathering.*;
import static entity.enrollment.QEnrollment.*;

@RequiredArgsConstructor
public class QueryDslUserRepository {
    private final JPAQueryFactory queryFactory;

    public Boolean existsByUsername(String username) {
        return queryFactory.selectFrom(user)
                .where(user.username.eq(username))
                .fetchFirst() != null;
    }

    public Boolean existsByNickname(String nickname) {
        return queryFactory.selectFrom(user)
                .where(user.nickname.eq(nickname))
                .fetchFirst() != null;
    }

    public List<User> findByEmail(String email) {
        return queryFactory.selectFrom(user)
                .where(user.email.eq(email))
                .fetch();
    }

    public Optional<User> findByIdFetchImage(Long userId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .leftJoin(user.profileImage, image).fetchJoin()
                        .where(user.id.eq(userId))
                        .fetchOne()
        );
    }

    public Optional<User> findAndTokenByUserId(Long userId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .leftJoin(user.tokens, fCMToken).fetchJoin()
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

    public List<User> findEnrollmentById(Long gatheringId, Long userId) {
        return queryFactory.select(enrollment.enrolledBy)
                .from(gathering)
                .join(gathering.enrollments, enrollment)
                .join(enrollment.enrolledBy, user)
                .where(
                        gathering.id.eq(gatheringId),
                        user.id.ne(userId))
                .fetch();
    }
}
