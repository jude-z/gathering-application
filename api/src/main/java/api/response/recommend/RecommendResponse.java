package api.response.recommend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static spring.myproject.dto.response.gathering.GatheringResponseDto.*;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendResponse {

    private String code;
    private String message;
    List<GatheringsResponse> content;

    public static RecommendResponse of(String code, String message, List<GatheringsResponse> content) {
        return new RecommendResponse(code, message, content);
    }
}
