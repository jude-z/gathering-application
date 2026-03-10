package infra.repository.fcm;

import entity.fcm.Topic;
import entity.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
public class JdbcFcmRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcFcmRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // === FCMTokenRepository ===

    public void deleteTokenByTokenValueIn(List<String> tokenValues) {
        if (CollectionUtils.isEmpty(tokenValues)) return;
        String placeholders = String.join(",", Collections.nCopies(tokenValues.size(), "?"));
        String sql = "DELETE FROM fcm_token WHERE token_value IN (" + placeholders + ")";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            for (int i = 0; i < tokenValues.size(); i++) {
                pstmt.setString(i + 1, tokenValues.get(i));
            }
            return pstmt;
        });
    }

    // === FCMTokenTopicRepository ===

    public void deleteTokenTopicByTokenValueIn(List<String> tokenValues) {
        if (CollectionUtils.isEmpty(tokenValues)) return;
        String placeholders = String.join(",", Collections.nCopies(tokenValues.size(), "?"));
        String sql = "DELETE FROM fcm_token_topic WHERE fcm_token_id IN " +
                "(SELECT id FROM fcm_token WHERE token_value IN (" + placeholders + "))";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            for (int i = 0; i < tokenValues.size(); i++) {
                pstmt.setString(i + 1, tokenValues.get(i));
            }
            return pstmt;
        });
    }

    // === UserTopicRepository ===

    public void deleteUserTopicByTopicAndUser(Topic topic, User user) {
        String sql = "DELETE FROM user_topic WHERE topic_id = ? AND user_id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, topic.getId());
            pstmt.setLong(2, user.getId());
            return pstmt;
        });
    }

}
