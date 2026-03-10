package infra.repository.chat;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Repository
public class JdbcChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcChatRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void readChatMessage(Long chatParticipantId, List<Long> messageIdList) {
        if(CollectionUtils.isEmpty(messageIdList)) return;
        String placeholders = String.join(",", Collections.nCopies(messageIdList.size(), "?"));
        String sql = "UPDATE read_status SET status = true " +
                "WHERE chat_participant_id = ? AND chat_message_id IN (" + placeholders + ") AND status = false";

        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, chatParticipantId);
            for (int i = 0; i < messageIdList.size(); i++) {
                pstmt.setLong(i + 2, messageIdList.get(i));
            }
            return pstmt;
        });
    }
}
