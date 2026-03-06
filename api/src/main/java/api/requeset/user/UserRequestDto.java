package api.requeset.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequestDto {
    @Data
    @Builder
    @AllArgsConstructor
    public static class SignUpRequest{
        @NotBlank(message = "cannot blank or null or space")
        private String username;
        @NotBlank(message = "cannot blank or null or space")
        private String password;
        @NotBlank(message = "cannot blank or null or space")
        private String email;
        @NotBlank(message = "cannot blank or null or space")
        private String address;
        @NotNull(message = "cannot null")
        private Integer age;
        @NotBlank(message = "cannot blank or null or space")
        private String hobby;
        @NotBlank(message = "cannot blank or null or space")
        private String nickname;
    }
    @Data
    @Builder
    @AllArgsConstructor
    public static class UpdateRequest{

        private String password;
        @NotBlank(message = "cannot blank or null or space")
        private String email;
        @NotBlank(message = "cannot blank or null or space")
        private String address;
        @NotNull(message = "cannot null")
        private Integer age;
        @NotBlank(message = "cannot blank or null or space")
        private String hobby;
        @NotBlank(message = "cannot blank or null or space")
        private String nickname;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class SignInRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String username;
        @NotBlank(message = "cannot blank or null or space")
        private String password;
        @NotBlank(message = "cannot blank or null or space")
        private String fcmToken;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NicknameCheckRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String nickname;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class IdCheckRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String username;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class EmailCertificationRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String clientId;
        @NotBlank(message = "cannot blank or null or space")
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class CheckCertificationRequest {
        @NotBlank(message = "cannot blank or null or space")
        private String certification;
        @NotBlank(message = "cannot blank or null or space")
        private String email;
    }

}