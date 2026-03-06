package api.response.attend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class AttendResponseDto {

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddAttendResponse {
        private String code;
        private String message;

        public static AddAttendResponse of(String code, String message) {
            return new AddAttendResponse(code, message);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DisAttendResponse {
        private String code;
        private String message;

        public static DisAttendResponse of(String code, String message) {
            return new DisAttendResponse(code, message);
        }
    }

}
