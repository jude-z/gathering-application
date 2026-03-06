package jdbc.repository.recommend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

@Repository
public class JdbcRecommendRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcRecommendRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int updateCount(Long gatheringId, LocalDate localDate, int val) {
        String sql = "insert into recommend(gathering_id, score, date) " +
                "values (?, 1, ?) " +
                "on duplicate key update score = score + ?";

        return jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, gatheringId);
            pstmt.setDate(2, Date.valueOf(localDate));
            pstmt.setInt(3, val);
            return pstmt;
        });
    }
}
