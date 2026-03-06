package api.response.like;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class LikeResponseDto {
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LikeResponse {

        private String code;
        private String message;

        public static LikeResponse of(String code, String message) {
            return new LikeResponse(code, message);
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DislikeResponse {

        private String code;
        private String message;

        public static DislikeResponse of(String code, String message) {
            return new DislikeResponse(code, message);
        }
    }
}
