package infra.repository.attend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import entity.attend.Attend;
import lombok.RequiredArgsConstructor;
import page.PageCalculator;
import page.PageInfo;
import page.PageableInfo;

import java.util.List;

import static entity.attend.QAttend.*;
import static entity.meeting.QMeeting.*;
import static entity.user.QUser.*;

@RequiredArgsConstructor
public class QueryDslAttendRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<Attend> findByUserIdAndMeetingId(PageableInfo pageableInfo, Long userId, Long meetingId){
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();
        List<Attend> content = queryFactory.selectFrom(attend)
                .join(attend.attendBy, user)
                .join(attend.meeting, meeting)
                .where(
                        user.id.eq(userId),
                        meeting.id.eq(meetingId))
                .offset(offset)
                .limit(limit)
                .orderBy(attend.date.desc())
                .fetch();

        Long totalCount = (queryFactory.select(attend.count())
                .from(attend)
                .join(attend.attendBy, user)
                .join(attend.meeting, meeting)
                .where(
                        user.id.eq(userId),
                        meeting.id.eq(meetingId))
                .fetchOne());
        int elementSize = content.size();
        PageInfo<List<Attend>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }


}
