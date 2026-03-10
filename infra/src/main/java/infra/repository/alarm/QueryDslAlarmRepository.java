package infra.repository.alarm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import infra.dto.PageInfo;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.alarm.Alarm;
import lombok.RequiredArgsConstructor;
import util.PageCalculator;

import java.util.List;

import static entity.alarm.QAlarm.*;
import static entity.user.QUser.*;

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
