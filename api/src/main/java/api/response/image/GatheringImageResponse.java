package api.response.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringImageResponse {
    private String code;
    private String message;
    @Builder.Default
    private List<String> urls = new ArrayList<>();
    private boolean hasNext;

    public static GatheringImageResponse of(String code, String message, List<String> urls,boolean hasNext) {
        return GatheringImageResponse.builder()
                .code(code)
                .message(message)
                .urls(urls)
                .hasNext(hasNext)
                .build();
    }
}
