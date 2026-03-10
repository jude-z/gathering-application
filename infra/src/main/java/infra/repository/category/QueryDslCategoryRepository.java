package infra.repository.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.category.Category;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static entity.category.QCategory.*;
import static entity.gathering.QGathering.*;

@RequiredArgsConstructor
public class QueryDslCategoryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Category> findBy(Long gatheringId, String name) {
        return Optional.ofNullable(
                queryFactory.select(gathering.category)
                        .from(gathering)
                        .leftJoin(gathering.category, category)
                        .where(
                                gathering.id.eq(gatheringId),
                                category.name.eq(name))
                        .fetchOne()
        );
    }
}
