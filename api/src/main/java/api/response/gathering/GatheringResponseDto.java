package api.response.gathering;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import response.gathering.querydto.GatheringsQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GatheringResponseDto {
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddGatheringResponse {

        private String code;
        private String message;
        private Long id;
        public static AddGatheringResponse of(String code, String message,Long id) {
            return new AddGatheringResponse(code, message,id);
        }

    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpdateGatheringResponse {

        private String code;
        private String message;
        private Long id;
        public static UpdateGatheringResponse of(String code, String message,Long id) {
            return new UpdateGatheringResponse(code, message,id);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GatheringResponse {

        private String code;
        private String message;
        private String title;
        private String content;
        private LocalDateTime registerDate;
        private String category;
        private String createdBy;
        private String createdByUrl;
        private List<ParticipatedBy> participatedByList;
        private String imageUrl;
        private boolean hasNext;
        private int count;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ParticipatedBy{
        private String participatedBy;
        private String participatedByNickname;
        private String participatedByUrl;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ParticipatedByResponse{
        private String code;
        private String message;
        private List<ParticipatedBy> participatedBy;
        private boolean hasNext;
        public static ParticipatedByResponse of(String code, String message, List<ParticipatedBy> participatedByList,boolean hasNext) {
            return new ParticipatedByResponse(code,message,participatedByList,hasNext);

        }
    }


    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GatheringCategoryResponse {

        private String code;
        private String message;
        List<GatheringsResponse> content;
        boolean hasNext;

        public static GatheringCategoryResponse of(String code, String message, List<GatheringsResponse> content, boolean hasNext) {
            return new GatheringCategoryResponse(code, message,content,hasNext);
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GatheringLikeResponse {

        private String code;
        private String message;
        List<GatheringsResponse> content;
        boolean hasNext;

        public static GatheringLikeResponse of(String code, String message, List<GatheringsResponse> content, boolean hasNext) {
            return new GatheringLikeResponse(code, message,content,hasNext);
        }

    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MainGatheringResponse {
        private String code;
        private String message;
        @Builder.Default
        private Map<String, CategoryTotalGatherings> map = new HashMap<>();

        public static MainGatheringResponse of(String code, String message, Map<String, CategoryTotalGatherings> categoryMap) {
            return new MainGatheringResponse(code, message, categoryMap);
        }
    }


    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GatheringsResponse {

        private Long id;
        private String title;
        private String content;
        private LocalDateTime registerDate;
        private String category;
        private String createdBy;
        private String url;
        private int count;

        public static GatheringsResponse from(GatheringsQuery gatheringsQuery, MyFunctionalInterface myFunctionalInterface){
            return GatheringsResponse.builder()
                    .id(gatheringsQuery.getId())
                    .title(gatheringsQuery.getTitle())
                    .createdBy(gatheringsQuery.getCreatedBy())
                    .registerDate(gatheringsQuery.getRegisterDate())
                    .category(gatheringsQuery.getCategory())
                    .content(gatheringsQuery.getContent())
                    .count(gatheringsQuery.getCount())
                    .url(myFunctionalInterface.execute(gatheringsQuery.getUrl()))
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MainGatheringElement {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime registerDate;
        private String category;
        private String createdBy;
        private String url;
        private int count;

        public static MainGatheringElement from(GatheringsQuery gatheringsQuery, MyFunctionalInterface myFunctionalInterface){
            return MainGatheringElement.builder().id(gatheringsQuery.getId())
                    .title(gatheringsQuery.getTitle())
                    .content(gatheringsQuery.getContent())
                    .registerDate(gatheringsQuery.getRegisterDate())
                    .category(gatheringsQuery.getCategory())
                    .createdBy(gatheringsQuery.getCreatedBy())
                    .count(gatheringsQuery.getCount())
                    .url(myFunctionalInterface.execute(gatheringsQuery.getUrl()))
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CategoryTotalGatherings {
        @Builder.Default
        private List<MainGatheringElement> totalGatherings = new ArrayList<>();
        boolean hasNext;

    }
}
