package api.common.mapper;

import api.requeset.user.UserRequestDto;
import entity.image.Image;
import entity.user.User;
import entity.user.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import static api.requeset.user.UserRequestDto.*;

public class UserMapper {
    public static void updateUser(User user, UpdateRequest updateRequest, PasswordEncoder passwordEncoder){
        String password = StringUtils.hasText(updateRequest.getPassword())
                ? passwordEncoder.encode(updateRequest.getPassword())
                : user.getPassword();
        user.change(password, updateRequest.getEmail(), updateRequest.getAddress(),
                updateRequest.getAge(), updateRequest.getHobby(), updateRequest.getNickname());
    }

    public static User toUser(SignUpRequest signUpRequest, Image image, PasswordEncoder passwordEncoder){
        return User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .email(signUpRequest.getEmail())
                .address(signUpRequest.getAddress())
                .age(signUpRequest.getAge())
                .hobby(signUpRequest.getHobby())
                .nickname(signUpRequest.getNickname())
                .role(Role.USER)
                .profileImage(image)
                .build();
    }
}
