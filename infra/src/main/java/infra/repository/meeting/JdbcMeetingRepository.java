package infra.repository.meeting;

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

    public void updateCount(Long meetingId, int val) {
        String sql = "update meeting set count = count + ? where id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, val);
            pstmt.setLong(2, meetingId);
            return pstmt;
        });
    }

}
