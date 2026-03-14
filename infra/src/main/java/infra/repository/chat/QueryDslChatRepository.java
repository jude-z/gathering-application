package infra.repository.chat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import infra.repository.dto.querydsl.chat.*;
import page.PageInfo;
import page.PageableInfo;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import entity.chat.ChatRoom;
import entity.chat.QChatMessage;
import entity.chat.QChatParticipant;
import entity.chat.QChatRoom;
import entity.chat.QReadStatus;
import entity.gathering.QGathering;
import entity.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import page.PageCalculator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static entity.chat.QChatParticipant.*;
import static entity.chat.QChatRoom.*;
import static entity.chat.QReadStatus.*;
import static entity.image.QImage.*;
import static entity.user.QUser.*;

@Repository
@RequiredArgsConstructor
public class QueryDslChatRepository {
    private final JPAQueryFactory queryFactory;

    // === ChatMessageRepository: fetchUnReadMessages ===

    public QueryDslPageResponse<ChatMessageProjection> fetchUnReadMessages(PageableInfo pageableInfo, Long chatId, Long userId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QChatParticipant cp = new QChatParticipant("cp");
        QChatMessage cm = new QChatMessage("cm");
        QUser u = new QUser("u");
        QChatRoom cr = new QChatRoom("cr");
        QChatParticipant cpp = new QChatParticipant("cpp");
        QUser uu = new QUser("uu");

        List<ChatMessageProjection> content = queryFactory.select(Projections.constructor(ChatMessageProjection.class,
                        cm.id, cr.id, cm.content, uu.id, uu.username, readStatus.status,
                        new CaseBuilder()
                                .when(cp.id.eq(cpp.id)).then(true)
                                .otherwise(false),
                        cm.createdAt))
                .from(readStatus)
                .leftJoin(readStatus.chatParticipant, cp)
                .leftJoin(readStatus.chatMessage, cm)
                .leftJoin(cp.user, u)
                .leftJoin(cm.chatRoom, cr)
                .leftJoin(cm.chatParticipant, cpp)
                .leftJoin(cpp.user, uu)
                .where(u.id.eq(userId), cr.id.eq(chatId), readStatus.status.eq(false))
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(readStatus.count())
                .from(readStatus)
                .leftJoin(readStatus.chatParticipant, cp)
                .leftJoin(readStatus.chatMessage, cm)
                .leftJoin(cp.user, u)
                .leftJoin(cm.chatRoom, cr)
                .where(u.id.eq(userId), cr.id.eq(chatId), readStatus.status.eq(false))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<ChatMessageProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === ChatParticipantRepository: fetchParticipant ===

    public QueryDslPageResponse<ParticipantProjection> fetchParticipant(PageableInfo pageableInfo, Long chatId, Long userId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QChatRoom c = new QChatRoom("c");
        QUser u = new QUser("u");

        List<ParticipantProjection> content = queryFactory.select(Projections.constructor(ParticipantProjection.class,
                        u.id, u.username, u.nickname, image.url, chatParticipant.status))
                .from(chatParticipant)
                .leftJoin(chatParticipant.chatRoom, c)
                .leftJoin(chatParticipant.user, u)
                .leftJoin(u.profileImage, image)
                .where(c.id.eq(chatId))
                .orderBy(new CaseBuilder()
                        .when(u.id.eq(userId)).then(0)
                        .otherwise(1).asc())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(chatParticipant.count())
                .from(chatParticipant)
                .leftJoin(chatParticipant.chatRoom, c)
                .where(c.id.eq(chatId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<ParticipantProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === ChatRoomRepository: fetchMyChatRooms ===

    public QueryDslPageResponse<MyChatRoomProjection> fetchMyChatRooms(PageableInfo pageableInfo, Long userId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QChatParticipant p = new QChatParticipant("p");
        QChatRoom c = new QChatRoom("c");
        QGathering g = new QGathering("g");
        QUser u = new QUser("u");
        QUser cr = new QUser("cr");

        List<MyChatRoomProjection> content = queryFactory.select(Projections.constructor(MyChatRoomProjection.class,
                        c.id, c.title, c.description, c.count, cr.username,
                        Expressions.constant(true),
                        unreadCountSubQuery(c, u),
                        g.title,
                        lastMessageContentSubQuery(c, u),
                        lastMessageDateTimeSubQuery(c, u)))
                .from(p)
                .leftJoin(p.chatRoom, c)
                .leftJoin(c.gathering, g)
                .leftJoin(p.user, u)
                .leftJoin(c.createdBy, cr)
                .where(u.id.eq(userId))
                .groupBy(c.id)
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(c.id.countDistinct())
                .from(p)
                .leftJoin(p.chatRoom, c)
                .leftJoin(p.user, u)
                .where(u.id.eq(userId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<MyChatRoomProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === ChatRoomRepository: fetchChatRooms ===

    public QueryDslPageResponse<ChatRoomProjection> fetchChatRooms(PageableInfo pageableInfo, Long userId, Long gatheringId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QChatRoom c = new QChatRoom("c");
        QGathering g = new QGathering("g");
        QUser u = new QUser("u");
        QChatParticipant cp = new QChatParticipant("cp");

        List<ChatRoomProjection> content = queryFactory.select(Projections.constructor(ChatRoomProjection.class,
                        c.id, c.title, c.description, c.count, u.username,
                        new CaseBuilder()
                                .when(cp.id.isNotNull()).then(true)
                                .otherwise(false),
                        new CaseBuilder()
                                .when(cp.id.isNotNull()).then(unreadCountSubQuery(c, u))
                                .otherwise(0L),
                        g.title,
                        lastMessageContentSubQuery(c, u),
                        lastMessageDateTimeSubQuery(c, u)))
                .from(c)
                .leftJoin(c.gathering, g)
                .leftJoin(c.createdBy, u)
                .leftJoin(cp).on(cp.chatRoom.id.eq(c.id), cp.user.id.eq(userId))
                .where(g.id.eq(gatheringId))
                .orderBy(
                        new CaseBuilder()
                                .when(cp.id.isNotNull()).then(0)
                                .otherwise(1).asc(),
                        c.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(c.count())
                .from(c)
                .leftJoin(c.gathering, g)
                .where(g.id.eq(gatheringId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<ChatRoomProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === ChatRoomRepository: fetchAbleChatRooms ===

    public QueryDslPageResponse<AbleChatRoomProjection> fetchAbleChatRooms(PageableInfo pageableInfo, Long userId, Long gatheringId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QChatRoom c = new QChatRoom("c");
        QGathering g = new QGathering("g");
        QUser u = new QUser("u");
        QChatParticipant cp = new QChatParticipant("cp");

        List<AbleChatRoomProjection> content = queryFactory.select(Projections.constructor(AbleChatRoomProjection.class,
                        c.id, c.title, c.description, c.count, u.username, g.title))
                .from(c)
                .leftJoin(c.gathering, g)
                .leftJoin(c.createdBy, u)
                .leftJoin(cp).on(cp.chatRoom.id.eq(c.id), cp.user.id.eq(userId))
                .where(cp.id.isNull(), g.id.eq(gatheringId))
                .orderBy(c.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(c.count())
                .from(c)
                .leftJoin(c.gathering, g)
                .leftJoin(cp).on(cp.chatRoom.id.eq(c.id), cp.user.id.eq(userId))
                .where(cp.id.isNull(), g.id.eq(gatheringId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<AbleChatRoomProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === ChatRoomRepository: fetchParticipateChatRooms ===

    public QueryDslPageResponse<ParticipateChatRoomProjection> fetchParticipateChatRooms(PageableInfo pageableInfo, Long userId, Long gatheringId) {
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();

        QChatRoom c = new QChatRoom("c");
        QGathering g = new QGathering("g");
        QUser u = new QUser("u");
        QChatParticipant cp = new QChatParticipant("cp");

        List<ParticipateChatRoomProjection> content = queryFactory.select(Projections.constructor(ParticipateChatRoomProjection.class,
                        c.id, c.title, c.description, c.count, u.username,
                        new CaseBuilder()
                                .when(cp.id.isNotNull()).then(unreadCountSubQuery(c, u))
                                .otherwise(0L),
                        g.title,
                        lastMessageContentSubQuery(c, u),
                        lastMessageDateTimeSubQuery(c, u)))
                .from(c)
                .leftJoin(c.gathering, g)
                .leftJoin(c.createdBy, u)
                .leftJoin(cp).on(cp.chatRoom.id.eq(c.id), cp.user.id.eq(userId))
                .where(cp.id.isNotNull(), g.id.eq(gatheringId))
                .orderBy(c.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(c.count())
                .from(c)
                .leftJoin(c.gathering, g)
                .leftJoin(cp).on(cp.chatRoom.id.eq(c.id), cp.user.id.eq(userId))
                .where(cp.id.isNotNull(), g.id.eq(gatheringId))
                .fetchOne();
        int elementSize = content.size();
        PageInfo<List<ParticipateChatRoomProjection>> pageInfo = PageCalculator.toPageInfo(content,offset, limit, totalCount, elementSize);
        return QueryDslPageResponse.of(content, pageInfo);
    }

    // === ChatRoomRepository: fetchChatRoomById ===

    public Optional<ChatRoom> fetchChatRoomById(Long chatRoomId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(chatRoom)
                        .leftJoin(chatRoom.createdBy, user).fetchJoin()
                        .where(chatRoom.id.eq(chatRoomId))
                        .fetchOne()
        );
    }

    // === Helper: 반복 서브쿼리 ===

    private Expression<Long> unreadCountSubQuery(QChatRoom c, QUser targetUser) {
        QReadStatus rs = new QReadStatus("rs_uc");
        QChatParticipant cp = new QChatParticipant("cp_uc");
        return JPAExpressions.select(rs.id.count())
                .from(rs)
                .leftJoin(rs.chatParticipant, cp)
                .where(
                        cp.chatRoom.id.eq(c.id),
                        rs.status.eq(false),
                        cp.user.id.eq(targetUser.id));
    }

    private Expression<String> lastMessageContentSubQuery(QChatRoom c, QUser targetUser) {
        QReadStatus rs1 = new QReadStatus("rs_lmc");
        QChatParticipant cp1 = new QChatParticipant("cp_lmc");
        QChatMessage cm1 = new QChatMessage("cm_lmc");
        QReadStatus rs2 = new QReadStatus("rs_lmc_max");
        QChatParticipant cp2 = new QChatParticipant("cp_lmc_max");

        return JPAExpressions.select(cm1.content)
                .from(rs1)
                .leftJoin(rs1.chatParticipant, cp1)
                .leftJoin(rs1.chatMessage, cm1)
                .where(
                        rs1.status.eq(false),
                        cp1.chatRoom.id.eq(c.id),
                        cp1.user.id.eq(targetUser.id),
                        rs1.id.eq(
                                JPAExpressions.select(rs2.id.max())
                                        .from(rs2)
                                        .leftJoin(rs2.chatParticipant, cp2)
                                        .where(
                                                rs2.status.eq(false),
                                                cp2.chatRoom.id.eq(c.id),
                                                cp2.user.id.eq(targetUser.id))
                        ));
    }

    private Expression<LocalDateTime> lastMessageDateTimeSubQuery(QChatRoom c, QUser targetUser) {
        QReadStatus rs1 = new QReadStatus("rs_lmd");
        QChatParticipant cp1 = new QChatParticipant("cp_lmd");
        QChatMessage cm1 = new QChatMessage("cm_lmd");
        QReadStatus rs2 = new QReadStatus("rs_lmd_max");
        QChatParticipant cp2 = new QChatParticipant("cp_lmd_max");

        return JPAExpressions.select(cm1.createdAt)
                .from(rs1)
                .leftJoin(rs1.chatParticipant, cp1)
                .leftJoin(rs1.chatMessage, cm1)
                .where(
                        rs1.status.eq(false),
                        cp1.chatRoom.id.eq(c.id),
                        cp1.user.id.eq(targetUser.id),
                        rs1.id.eq(
                                JPAExpressions.select(rs2.id.max())
                                        .from(rs2)
                                        .leftJoin(rs2.chatParticipant, cp2)
                                        .where(
                                                rs2.status.eq(false),
                                                cp2.chatRoom.id.eq(c.id),
                                                cp2.user.id.eq(targetUser.id))
                        ));
    }
}
