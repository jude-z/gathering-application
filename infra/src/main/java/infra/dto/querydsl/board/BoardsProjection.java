package infra.dto.querydsl.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardsProjection {
    private Long id;
    private String title;
    private String description;
    private String nickname;
    private LocalDateTime registerDate;
    private String url;
}
