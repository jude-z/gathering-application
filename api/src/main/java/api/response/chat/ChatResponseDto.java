package api.response.chat;

import entity.chat.ChatRoom;
import entity.user.User;
import infra.repository.dto.querydsl.chat.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatResponseDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AddChatRoomResponse {

        private String code;
        private String message;
        private Long id;
        public static AddChatRoomResponse of(String code, String message,Long id){
            return new AddChatRoomResponse(code,message,id);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LeaveChatResponse {
        String code;
        String message;

        public static LeaveChatResponse of(String code, String message) {
            return new LeaveChatResponse(code, message);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReadChatMessageResponse {
        private String code;
        private String message;

        public static ReadChatMessageResponse of(String code, String message) {
            return new ReadChatMessageResponse(code, message);
        }
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class ChatMessagesResponse {
        private String code;
        private String message;
        private List<ChatMessageProjection> content;

        public static ChatMessagesResponse of(String code, String message, List<ChatMessageProjection> content) {
            return new ChatMessagesResponse(code, message, content);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AttendChatResponse {
        private String code;
        private String message;

        public static AttendChatResponse of(String code, String message) {
            return new AttendChatResponse(code, message);
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MyChatRoomResponse {
        private String code;
        private String message;
        private List<MyChatRoomProjection> content;
        private boolean hasNext;

        public static MyChatRoomResponse of(String code, String message, List<MyChatRoomProjection> content, boolean hasNext) {
            return new MyChatRoomResponse(code, message, content, hasNext);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChatRoomResponse {
        private String code;
        private String message;
        private List<ChatRoomProjection> content;
        private boolean hasNext;

        public static ChatRoomResponse of(String code, String message, List<ChatRoomProjection> content, boolean hasNext) {
            return new ChatRoomResponse(code, message, content, hasNext);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AbleChatRoomResponse {
        private String code;
        private String message;
        private List<AbleChatRoomProjection> content;
        private boolean hasNext;

        public static AbleChatRoomResponse of(String code, String message, List<AbleChatRoomProjection> content, boolean hasNext) {
            return new AbleChatRoomResponse(code, message, content, hasNext);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ParticipateChatRoomResponse {
        private String code;
        private String message;
        private List<ParticipateChatRoomProjection> content;
        private boolean hasNext;

        public static ParticipateChatRoomResponse of(String code, String message, List<ParticipateChatRoomProjection> content, boolean hasNext) {
            return new ParticipateChatRoomResponse(code, message, content, hasNext);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FetchChatRoomResponse {
        private String code;
        private String message;
        private String title;
        private String description;
        private Long createdById;
        private String createdByUsername;
        private String createdByNickname;
        private int count;


        public static FetchChatRoomResponse of(String code, String message, ChatRoom chatRoom) {
            User user = chatRoom.getCreatedBy();
            return FetchChatRoomResponse.builder()
                    .code(code)
                    .message(message)
                    .title(chatRoom.getTitle())
                    .description(chatRoom.getDescription())
                    .count(chatRoom.getCount())
                    .createdById(user != null ? user.getId() : null)
                    .createdByUsername(user != null ? user.getUsername() : null)
                    .createdByNickname(user != null ? user.getNickname() : null)
                    .build();
        }
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Data
        public static class FetchParticipantResponse {
            private String code;
            private String message;
            private List<ParticipantProjection> content;
            private boolean hasNext;


            public static FetchParticipantResponse of(String code, String message, List<ParticipantProjection> content, boolean hasNext) {
                return new FetchParticipantResponse(code, message, content, hasNext);
            }

        }


    }




}
