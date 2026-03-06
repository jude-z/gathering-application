package api.response.enrollment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class EnrollResponseDto {
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EnrollGatheringResponse {
        private String code;
        private String message;

        public static EnrollGatheringResponse of(String code, String message) {
            return new EnrollGatheringResponse(code, message);
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DisEnrollGatheringResponse {
        private String code;
        private String message;

        public static DisEnrollGatheringResponse of(String code, String message) {
            return new DisEnrollGatheringResponse(code, message);
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PermitEnrollmentResponse {
        private String code;
        private String message;

        public static PermitEnrollmentResponse of(String code, String message) {
            return new PermitEnrollmentResponse(code, message);
        }
    }
}
