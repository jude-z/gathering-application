package infra.dto.querydsl.meeting;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingProjection {

    private Long id;
    private String title;
    private String createdBy;
    private String createdByNickname;
    private String createdByUrl;
    private String attendedBy;
    private String attendByNickname;
    private String attendedByUrl;
    private LocalDateTime meetingDate;
    private LocalDateTime endDate;
    private String content;
    private int count;
    private String url;
}
