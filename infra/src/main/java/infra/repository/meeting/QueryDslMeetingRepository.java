package infra.repository.meeting;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import page.PageInfo;
import page.PageableInfo;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.meeting.MeetingProjection;
import infra.repository.dto.querydsl.meeting.MeetingsProjection;
import entity.image.QImage;
import entity.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import page.PageCalculator;

import java.util.List;

import static entity.attend.QAttend.*;
import static entity.meeting.QMeeting.*;

@Repository
@RequiredArgsConstructor
public class QueryDslMeetingRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDslPageResponse<MeetingProjection> meetingDetail(Long meetingId) {

        QUser creator = new QUser("creator");
        QUser attendee = new QUser("attendee");
        QImage creatorImage = new QImage("creatorImage");
        QImage attendeeImage = new QImage("attendeeImage");
        QImage meetingImage = new QImage("meetingImage");

        List<MeetingProjection> content = queryFactory
                .select(Projections.constructor(MeetingProjection.class,
                        meeting.id,
                        meeting.title,
                        creator.username,
                        creator.nickname,
                        creatorImage.url,
                        attendee.username,
                        attendee.nickname,
                        attendeeImage.url,
                        meeting.meetingDate,
                        meeting.endDate,
                        meeting.content,
                        meeting.count,
                        meetingImage.url
                ))
                .from(meeting)
                .leftJoin(meeting.attends, attend)
                .leftJoin(attend.attendBy, attendee)
                .leftJoin(attendee.profileImage, attendeeImage)
                .leftJoin(meeting.createdBy, creator)
                .leftJoin(creator.profileImage, creatorImage)
                .leftJoin(meeting.image, meetingImage)
                .where(meeting.id.eq(meetingId))
                .fetch();

        Long totalCount = queryFactory
                .select(attend.count())
                .from(meeting)
                .leftJoin(meeting.attends, attend)
                .where(meeting.id.eq(meetingId))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<MeetingProjection>> pageInfo = PageCalculator.toPageInfo(content, -1, -1, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    public QueryDslPageResponse<MeetingsProjection> meetings(PageableInfo pageableInfo, Long gatheringId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QUser creator = new QUser("creator");
        QUser attendee = new QUser("attendee");
        QImage meetingImage = new QImage("meetingImage");
        QImage attendeeImage = new QImage("attendeeImage");

        List<MeetingsProjection> content = queryFactory
                .select(Projections.constructor(MeetingsProjection.class,
                        meeting.id,
                        meeting.title,
                        creator.username,
                        creator.nickname,
                        meeting.meetingDate,
                        meeting.endDate,
                        meeting.content,
                        meeting.count,
                        meetingImage.url,
                        attendee.id,
                        attendeeImage.url
                ))
                .from(meeting)
                .leftJoin(meeting.createdBy, creator)
                .leftJoin(meeting.image, meetingImage)
                .leftJoin(meeting.attends, attend)
                .leftJoin(attend.attendBy, attendee)
                .leftJoin(attendee.profileImage, attendeeImage)
                .where(meeting.gathering.id.eq(gatheringId))
                .orderBy(meeting.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory
                .select(meeting.count())
                .from(meeting)
                .where(meeting.gathering.id.eq(gatheringId))
                .fetchOne();

        int elementSize = content.size();
        PageInfo<List<MeetingsProjection>> pageInfo = PageCalculator.toPageInfo(content, offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }
}
