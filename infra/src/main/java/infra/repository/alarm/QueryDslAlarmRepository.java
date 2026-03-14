package infra.repository.alarm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import page.PageInfo;
import page.PageableInfo;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import entity.alarm.Alarm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import page.PageCalculator;

import java.util.List;

import static entity.alarm.QAlarm.*;
import static entity.user.QUser.*;

@Repository
@RequiredArgsConstructor
public class QueryDslAlarmRepository {

    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<Alarm> findUncheckedAlarm(PageableInfo pageableInfo, Long userId){
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();
        List<Alarm> content = queryFactory.selectFrom(alarm)
                .join(alarm.user, user)
                .where(
                        user.id.eq(userId),
                        alarm.checked.eq(false))
                .offset(offset)
                .limit(limit)
                .orderBy(alarm.date.desc())
                .fetch();

        Long totalCount = (queryFactory.select(alarm.count())
                .from(alarm)
                .join(alarm.user, user)
                .where(
                        user.id.eq(userId),
                        alarm.checked.eq(false))
                .fetchOne());
        int elementSize = content.size();
        PageInfo<List<Alarm>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content,pageInfo);

    }

    public QueryDslPageResponse<Alarm> findCheckedAlarm(PageableInfo pageableInfo, Long userId){
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();
        List<Alarm> content = queryFactory.selectFrom(alarm)
                .join(alarm.user, user)
                .where(
                        user.id.eq(userId),
                        alarm.checked.eq(true))
                .offset(offset)
                .limit(limit)
                .orderBy(alarm.date.desc())
                .fetch();

        Long totalCount = (queryFactory.select(alarm.count())
                .from(alarm)
                .join(alarm.user, user)
                .where(
                        user.id.eq(userId),
                        alarm.checked.eq(true))
                .fetchOne());
        int elementSize = content.size();
        PageInfo<List<Alarm>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }


}
