package infra.repository.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.like.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static entity.like.QLike.*;
import static entity.gathering.QGathering.*;
import static entity.user.QUser.*;

@Repository
@RequiredArgsConstructor
public class QueryDslLikeRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Like> findLike(Long userId, Long gatheringId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(like)
                        .leftJoin(like.gathering, gathering)
                        .leftJoin(like.likedBy, user)
                        .where(
                                gathering.id.eq(gatheringId),
                                user.id.eq(userId))
                        .fetchOne()
        );
    }
}
