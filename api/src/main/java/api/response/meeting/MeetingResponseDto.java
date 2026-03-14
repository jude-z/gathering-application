package api.response.meeting;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import infra.repository.dto.querydsl.meeting.MeetingsProjection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingResponseDto {
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddMeetingResponse {
        private String code;
        private String message;
        private Long id;

        public static AddMeetingResponse of(String code, String message,Long id) {
            return new AddMeetingResponse(code, message,id);
        }

    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpdateMeetingResponse {
        private String code;
        private String message;
        private Long id;
        public static UpdateMeetingResponse of(String code, String message,Long id) {
            return new UpdateMeetingResponse(code, message,id);
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteMeetingResponse {
        private String code;
        private String message;
        public static DeleteMeetingResponse of(String code, String message) {
            return new DeleteMeetingResponse(code, message);
        }

    }
    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MeetingResponse {

        private Long id;
        private String title;
        private String createdBy;
        private String createdByNickname;
        private String createdByUrl;
        private List<String> attendedBy;
        private List<String> attendedByNickname;
        private List<String> attendedByUrl;
        private LocalDateTime meetingDate;
        private LocalDateTime endDate;
        private String content;
        private String meetingUrl;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MeetingsResponse {

        private String code;
        private String message;
        private List<MeetingElement> content;
        boolean hasNext;
        public static MeetingsResponse of(String code, String message,List<MeetingElement> content,boolean hasNext) {
            return new MeetingsResponse(code, message,content,hasNext);
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MeetingElement {

        private Long id;
        private String title;
        private String createdBy;
        private LocalDateTime meetingDate;
        private LocalDateTime endDate;
        private String content;
        private int count;
        private String url;
        private List<Participated> participatedList;

        public static MeetingElement from(MeetingsProjection projection, String baseUrl) {
            return MeetingElement.builder()
                    .id(projection.getId())
                    .title(projection.getTitle())
                    .createdBy(projection.getCreatedBy())
                    .meetingDate(projection.getMeetingDate())
                    .endDate(projection.getEndDate())
                    .content(projection.getContent())
                    .count(projection.getCount())
                    .url(baseUrl + projection.getUrl())
                    .participatedList(new ArrayList<>())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class Participated {
        private Long id;
        private String imageUrl;
    }

}
