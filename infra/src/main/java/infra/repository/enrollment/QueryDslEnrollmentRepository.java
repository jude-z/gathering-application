package infra.repository.enrollment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static entity.enrollment.QEnrollment.*;
import static entity.gathering.QGathering.*;
import static entity.user.QUser.*;

@Repository
@RequiredArgsConstructor
public class QueryDslEnrollmentRepository {
    private final JPAQueryFactory queryFactory;

    public Enrollment existEnrollment(Long gatheringId, Long userId) {
        return queryFactory.selectFrom(enrollment)
                .where(
                        enrollment.gathering.id.eq(gatheringId),
                        enrollment.enrolledBy.id.eq(userId))
                .fetchOne();
    }

    public Optional<Enrollment> findEnrollment(Long gatheringId, Long userId, boolean accepted) {
        return Optional.ofNullable(
                queryFactory.selectFrom(enrollment)
                        .join(enrollment.enrolledBy, user).fetchJoin()
                        .where(
                                enrollment.gathering.id.eq(gatheringId),
                                user.id.eq(userId),
                                enrollment.accepted.eq(accepted))
                        .fetchOne()
        );
    }

    public Optional<Enrollment> findEnrollmentEnrolledByAndTokensById(Long enrollmentId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(enrollment)
                        .leftJoin(enrollment.enrolledBy, user).fetchJoin()
                        .where(
                                enrollment.id.eq(enrollmentId),
                                enrollment.accepted.eq(false))
                        .fetchOne()
        );
    }

}
