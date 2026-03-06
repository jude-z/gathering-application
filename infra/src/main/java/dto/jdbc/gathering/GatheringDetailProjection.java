package dto.jdbc.gathering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatheringDetailProjection {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime registerDate;
    private String category;
    private String createdBy;
    private String createdByUrl;
    private String participatedBy;
    private String participatedByNickname;
    private String participatedByUrl;
    private String url;
    private int count;
}
