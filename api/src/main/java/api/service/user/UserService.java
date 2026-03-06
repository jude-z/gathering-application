package api.service.user;

import api.response.ApiDataResponse;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import api.service.image.ImageUploadService;
import common.ImageUrlConverter;
import entity.image.Image;
import entity.user.User;
import exception.CommonException;
import jakarta.servlet.http.HttpServletResponse;
import jpa.repository.certification.CertificationRepository;
import jpa.repository.image.ImageRepository;
import jpa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import querydsl.repository.certification.QueryDslCertificationRepository;
import querydsl.repository.image.QueryDslImageRepository;
import querydsl.repository.user.QueryDslUserRepository;

import java.io.IOException;
import java.util.List;

import static api.requeset.user.UserRequestDto.*;
import static api.response.user.UserResponseDto.*;
import static exception.Status.*;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final CertificationRepository certificationRepository;
    private final QueryDslUserRepository queryDslUserRepository;
    private final QueryDslImageRepository queryDslImageRepository;
    private final QueryDslCertificationRepository queryDslCertificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;
    private final ImageUrlConverter imageUrlConverter;


    public ApiResponse idCheck(IdCheckRequest idCheckRequest) {
        boolean idCheck = !userRepository.existsByUsername(idCheckRequest.getUsername());
        if(!idCheck) throw new CommonException(EXIST_USER);
        return ApiStatusResponse.of(SUCCESS);
    }
    public ApiResponse nicknameCheck(NicknameCheckRequest nicknameCheckRequest) {
        boolean nicknameCheck = !userRepository.existsByNickname(nicknameCheckRequest.getNickname());
        if(!nicknameCheck) throw new CommonException(EXIST_USER);
        return ApiStatusResponse.of(SUCCESS);

    }

    public ApiResponse signUp(SignUpRequest signUpRequest, MultipartFile file) throws IOException {

            Image image = null;
            if(!file.isEmpty()) {
                String contentType = file.getContentType();
                String url = imageUploadService.upload(file);
                image = Image.builder()
                        .contentType(contentType)
                        .url(url)
                        .build();
                imageRepository.save(image);
            }
            User user = UserFactory.toUser(signUpRequest,image,passwordEncoder);
            userRepository.save(user);
            return ApiStatusResponse.of(SUCCESS);

    }

    public ApiResponse update(UpdateRequest updateRequest, Long userId,MultipartFile file,Long id) throws IOException {
            User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(NOT_FOUND_USER));
            boolean authorize = userId.equals(id);
            if(!authorize) throw new CommonException(NOT_AUTHORIZE);
            Image image = null;
            if(file != null && !file.isEmpty()){
                String contentType = file.getContentType();
                String url = imageUploadService.upload(file);
                image = Image.builder()
                        .contentType(contentType)
                        .url(url)
                        .build();
                imageRepository.save(image);
                user.changeProfileImage(image);
            }
            user.change(updateRequest,passwordEncoder);
            return ApiDataResponse.of(userId,SUCCESS);
    }

    public ApiResponse signIn(SignInRequest signInRequest, HttpServletResponse response) {

            User user = queryDslUserRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new CommonException(NOT_FOUND_USER));
            boolean matches = passwordEncoder.matches(signInRequest.getPassword(), user.getPassword());
            if(!matches){
                throw new CommonException(UN_CORRECT_PASSWORD);
            }
            return ApiStatusResponse.of(SUCCESS);
    }

    public ApiResponse emailCertification(EmailCertificationRequest emailCertificationRequest) {

            List<User> users = queryDslUserRepository.findByEmail(emailCertificationRequest.getEmail());
            if(!users.isEmpty()) throw new CommonException(DUPLICATE_EMAIL);
            //TODO: Kafka Producer
            return ApiStatusResponse.of(SUCCESS);
    }

    public ApiResponse checkCertification(CheckCertificationRequest checkCertificationRequest) {
        String certification = queryDslCertificationRepository.findCertificationByEmail(checkCertificationRequest.getEmail());
        if(!StringUtils.hasText(certification)) throw new CommonException(NOT_FOUND_CERTIFICATION);
        if(!certification.equals(checkCertificationRequest.getCertification())) throw new CommonException(UN_CORRECT_CERTIFICATION);
        return ApiStatusResponse.of(SUCCESS);
    }

    public ApiResponse fetchUser(Long userId) {
        User user = queryDslUserRepository.findByIdFetchImage(userId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_USER));
        UserResponse userResponse = UserResponse.from(SUCCESS,user,imageUrlConverter);
        return ApiDataResponse.of(userResponse,SUCCESS);
    }
}
