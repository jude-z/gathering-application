package api.response.user;

import api.response.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import entity.user.User;
import exception.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import util.ImageUrlProcess;


public class UserResponseDto {





    @Getter
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserResponse extends Data{

        private final String code;
        private final String message;
        private Long id;
        private String username;
        private String email;
        private String address;
        private Integer age;
        private String hobby;
        private String nickname;
        private String imageUrl;

        public static UserResponse of(String code, String message) {
            return UserResponse.builder()
                    .code(code)
                    .message(message)
                    .build();
        }

        public static UserResponse from(Status status, User user, ImageUrlProcess imageUrlProcess) {
            return UserResponse.builder()
                    .code(status.getCode())
                    .message(status.getContent())
                    .id(user.getId())
                    .age(user.getAge())
                    .address(user.getAddress())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .hobby(user.getHobby())
                    .imageUrl(imageUrlProcess.convert(user.getProfileImage().getUrl()))
                    .username(user.getUsername())
                    .build();
        }
    }
}
