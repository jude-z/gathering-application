package infra.repository.gathering;

import infra.dto.jdbc.gathering.GatheringDetailProjection;
import infra.dto.jdbc.gathering.MainGatheringsProjection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class JdbcGatheringRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGatheringRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GatheringDetailProjection> gatheringDetail(Long gatheringId) {
        String sql = "select " +
                "g.id as id, g.title as title, g.content as content, g.register_date as registerDate, " +
                "ca.name as category, cr.username as createdBy, crm.url as createdByUrl, " +
                "u.username as participatedBy, u.nickname as participatedByNickname, " +
                "pm.url as participatedByUrl, im.url as url, g.count as count " +
                "from gathering g " +
                "join category ca on ca.id = g.category_id " +
                "join user cr on g.user_id = cr.id " +
                "join image crm on cr.image_id = crm.id " +
                "join image im on g.image_id = im.id " +
                "left join " +
                "(select e.* " +
                "from enrollment e " +
                "left join user u on u.id = e.user_id " +
                "left join gathering ge on ge.id = e.gathering_id and ge.id = ? " +
                "order by u.id " +
                "limit 10) " +
                "e on e.gathering_id = g.id " +
                "left join user u on u.id = e.user_id " +
                "left join image pm on u.image_id = pm.id " +
                "where g.id = ? " +
                "order by u.id";
        return jdbcTemplate.query(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, gatheringId);
            pstmt.setLong(2, gatheringId);
            return pstmt;
        }, gatheringDetailRowMapper());
    }

    public List<MainGatheringsProjection> gatherings() {
        String sql = "select id, title, content, registerDate, category, createdBy, url, count from ( " +
                "  select g.id as id, g.title as title, g.content as content, g.register_date as registerDate, ca.name as category, " +
                "         cr.username as createdBy, im.url as url, g.count as count, " +
                "         row_number() over (partition by ca.name order by g.count desc) as rownum " +
                "  from gathering g " +
                "  left join category ca on g.category_id = ca.id " +
                "  left join user cr on g.user_id = cr.id " +
                "  left join image im on g.image_id = im.id " +
                ") as subquery " +
                "where rownum between 1 and 9";
        return jdbcTemplate.query(con -> con.prepareStatement(sql), mainGatheringsRowMapper());
    }

    public List<MainGatheringsProjection> subGatherings(String name) {
        String sql = "SELECT " +
                "g.id as id, g.title as title, g.content as content, g.register_date as registerDate, " +
                "g.category_name AS category, " +
                "cr.username AS createdBy, " +
                "im.url as url, " +
                "g.count as count " +
                "FROM ( " +
                "SELECT " +
                "g.*, " +
                "ca.name AS category_name " +
                "FROM gathering g " +
                "JOIN category ca ON ca.id = g.category_id " +
                "WHERE ca.name = ? " +
                "ORDER BY g.count DESC " +
                "LIMIT 9 " +
                ") g " +
                "LEFT JOIN user cr ON g.user_id = cr.id " +
                "LEFT JOIN image im ON g.image_id = im.id";
        return jdbcTemplate.query(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            return pstmt;
        }, mainGatheringsRowMapper());
    }

    public void updateCount(Long gatheringId, int val) {
        String sql = "update gathering set count = count + ? where id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, val);
            pstmt.setLong(2, gatheringId);
            return pstmt;
        });
    }

    private RowMapper<GatheringDetailProjection> gatheringDetailRowMapper() {
        return (rs, rowNum) -> GatheringDetailProjection.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .content(rs.getString("content"))
                .registerDate(rs.getTimestamp("registerDate") != null
                        ? rs.getTimestamp("registerDate").toLocalDateTime() : null)
                .category(rs.getString("category"))
                .createdBy(rs.getString("createdBy"))
                .createdByUrl(rs.getString("createdByUrl"))
                .participatedBy(rs.getString("participatedBy"))
                .participatedByNickname(rs.getString("participatedByNickname"))
                .participatedByUrl(rs.getString("participatedByUrl"))
                .url(rs.getString("url"))
                .count(rs.getInt("count"))
                .build();
    }

    private RowMapper<MainGatheringsProjection> mainGatheringsRowMapper() {
        return (rs, rowNum) -> MainGatheringsProjection.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .content(rs.getString("content"))
                .registerDate(rs.getTimestamp("registerDate") != null
                        ? rs.getTimestamp("registerDate").toLocalDateTime() : null)
                .category(rs.getString("category"))
                .createdBy(rs.getString("createdBy"))
                .url(rs.getString("url"))
                .count(rs.getInt("count"))
                .build();
    }
}
