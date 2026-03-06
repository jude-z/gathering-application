package api.requeset.gathering;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class GatheringRequestDto {
    @Data
    @Builder
    @AllArgsConstructor
    public static class AddGatheringRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String title;
        @NotBlank(message = "cannot blank or null or space")
        private String content;
        @NotBlank(message = "cannot blank or null or space")
        private String category;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class UpdateGatheringRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String title;
        @NotBlank(message = "cannot blank or null or space")
        private String content;
        @NotBlank(message = "cannot blank or null or space")
        private String category;

    }
}