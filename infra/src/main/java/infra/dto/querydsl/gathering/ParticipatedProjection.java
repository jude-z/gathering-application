package infra.dto.querydsl.gathering;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipatedProjection {
    private String participatedBy;
    private String participatedByNickname;
    private String participatedByUrl;
}
