package api.response.gathering;

import com.fasterxml.jackson.annotation.JsonInclude;
import infra.dto.querydsl.gathering.GatheringsProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.ImageUrlProcess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class GatheringResponseDto {



    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GatheringResponse {

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

        public static MainGatheringElement from(GatheringsProjection projection, ImageUrlProcess imageUrlProcess){
            return MainGatheringElement.builder()
                    .id(projection.getId())
                    .title(projection.getTitle())
                    .content(projection.getContent())
                    .registerDate(projection.getRegisterDate())
                    .category(projection.getCategory())
                    .createdBy(projection.getCreatedBy())
                    .count(projection.getCount())
                    .url(imageUrlProcess.convert(projection.getUrl()))
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
