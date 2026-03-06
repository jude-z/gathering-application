package api.response.alarm;

import com.fasterxml.jackson.annotation.JsonInclude;
import entity.alarm.Alarm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class AlarmResponseDto {





    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlarmResponses {

        private String code;
        private String message;
        private List<AlarmElement> content;
        boolean hasNext;

        public static AlarmResponses of(String code, String message, List<AlarmElement> content, boolean hasNext) {
            return new AlarmResponses(code,message,content,hasNext);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlarmElement {

        private String content;
        private LocalDateTime date;
        private Boolean checked;

        public static AlarmElement from(Alarm alarm){
            return AlarmElement.builder()
                    .content(alarm.getContent())
                    .date(alarm.getDate())
                    .checked(alarm.isChecked())
                    .build();
        }
    }
}
