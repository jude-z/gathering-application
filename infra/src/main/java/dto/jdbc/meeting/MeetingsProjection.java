package dto.jdbc.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingsProjection {
    private Long id;
    private String title;
    private String createdBy;
    private String createdByNickname;
    private LocalDateTime meetingDate;
    private LocalDateTime endDate;
    private String content;
    private int count;
    private String url;
    private Long participatedId;
    private String participatedImageUrl;
}
