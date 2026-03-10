package api.response.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import infra.dto.querydsl.board.BoardProjection;
import infra.dto.querydsl.board.BoardsProjection;

import java.time.LocalDateTime;
import java.util.List;

public class BoardResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddBoardResponse {

        private String code;
        private String message;
        private Long id;
        public static AddBoardResponse of(String successCode, String successMessage,Long id) {
            return new AddBoardResponse(successCode,successMessage,id);
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BoardResponse {
        private String title;
        private String description;
        private List<String> imageUrls;
        private String username;
        private String userImageUrl;
        private LocalDateTime registerDate;

        public static BoardResponse from(List<BoardProjection> boardProjections, List<String> imageUrls, String userImageUrl) {
            return BoardResponse.builder()
                    .title(boardProjections.getFirst().getTitle())
                    .description(boardProjections.getFirst().getDescription())
                    .registerDate(boardProjections.getFirst().getRegisterDate())
                    .imageUrls(imageUrls)
                    .username(boardProjections.getFirst().getUsername())
                    .userImageUrl(userImageUrl)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BoardsResponse {

        private String code;
        private String message;
        private List<BoardElement> content;
        boolean hasNext;

        public static BoardsResponse of(String code, String message, List<BoardElement> content,boolean hasNext) {
            return BoardsResponse.builder()
                    .code(code)
                    .message(message)
                    .content(content)
                    .hasNext(hasNext)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardElement {
        private Long id;
        private String title;
        private String description;
        private String nickname;
        private LocalDateTime registerDate;
        private String url;

        public static BoardElement from(BoardsProjection projection, String baseUrl) {
            return BoardElement.builder()
                    .id(projection.getId())
                    .title(projection.getTitle())
                    .description(projection.getDescription())
                    .nickname(projection.getNickname())
                    .registerDate(projection.getRegisterDate())
                    .url(baseUrl + projection.getUrl())
                    .build();

        }
    }
}
