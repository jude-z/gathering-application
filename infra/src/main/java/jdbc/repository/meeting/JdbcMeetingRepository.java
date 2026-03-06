package jdbc.repository.meeting;

import dto.jdbc.meeting.MeetingsProjection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcMeetingRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMeetingRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<MeetingsProjection> meetings(Integer offset, Long gatheringId) {
        String sql = """
                select m.id as id, m.title as title, cr.username as createdBy, cr.nickname as createdByNickname,
                       m.meeting_date as meetingDate, m.end_date as endDate,
                       m.content as content, m.count as count, i.url as url,
                       au.id as participatedId, pi.url as participatedImageUrl
                from (
                    select m2.id
                    from meeting m2
                    where m2.gathering_id = ?
                    order by m2.id asc
                    limit 9 offset ?
                ) as sub
                join meeting m on m.id = sub.id
                left join user cr on m.user_id = cr.id
                left join image i on m.image_id = i.id
                left join gathering g on m.gathering_id = g.id
                left join attend a on a.meeting_id = m.id
                left join user au on a.user_id = au.id
                left join image pi on au.image_id = pi.id
                where g.id = ?
                """;
        return jdbcTemplate.query(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, gatheringId);
            pstmt.setInt(2, offset);
            pstmt.setLong(3, gatheringId);
            return pstmt;
        }, meetingsRowMapper());
    }

    public void updateCount(Long meetingId, int val) {
        String sql = "update meeting set count = count + ? where id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, val);
            pstmt.setLong(2, meetingId);
            return pstmt;
        });
    }

    private RowMapper<MeetingsProjection> meetingsRowMapper() {
        return (rs, rowNum) -> MeetingsProjection.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .createdBy(rs.getString("createdBy"))
                .createdByNickname(rs.getString("createdByNickname"))
                .meetingDate(rs.getTimestamp("meetingDate") != null
                        ? rs.getTimestamp("meetingDate").toLocalDateTime() : null)
                .endDate(rs.getTimestamp("endDate") != null
                        ? rs.getTimestamp("endDate").toLocalDateTime() : null)
                .content(rs.getString("content"))
                .count(rs.getInt("count"))
                .url(rs.getString("url"))
                .participatedId(rs.getObject("participatedId") != null
                        ? rs.getLong("participatedId") : null)
                .participatedImageUrl(rs.getString("participatedImageUrl"))
                .build();
    }
}
