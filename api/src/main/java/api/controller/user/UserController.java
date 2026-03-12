package api.controller.user;

import api.common.resolver.annotation.Username;
import api.response.ApiResponse;
import api.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static api.requeset.user.UserRequestDto.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/id-check")
    public ResponseEntity<ApiResponse> idCheck(@RequestBody IdCheckRequest idCheckRequest) {
        ApiResponse apiResponse = userService.idCheck(idCheckRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/auth/nickname-check")
    public ResponseEntity<ApiResponse> nicknameCheck(@RequestBody NicknameCheckRequest nicknameCheckRequest) {
        ApiResponse apiResponse = userService.nicknameCheck(nicknameCheckRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/auth/sign-up", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<ApiResponse> signUp(@RequestPart SignUpRequest signUpRequest,
                                              @RequestPart(name = "file") MultipartFile file) throws IOException {
        ApiResponse apiResponse = userService.signUp(signUpRequest, file);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/auth/update/{userId}", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<ApiResponse> update(@RequestPart UpdateRequest updateRequest,
                                              @RequestPart(required = false, name = "file") MultipartFile file,
                                              @PathVariable Long userId, @Username Long id) throws IOException {
        ApiResponse apiResponse = userService.update(updateRequest, userId, file, id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/auth/user/{userId}")
    public ResponseEntity<ApiResponse> fetchUser(@PathVariable Long userId){
        ApiResponse apiResponse = userService.fetchUser(userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<ApiResponse> signIn(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        ApiResponse apiResponse = userService.signIn(signInRequest, response);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/auth/email-certification")
    public ResponseEntity<ApiResponse> emailCertification(@RequestBody EmailCertificationRequest emailCertificationRequest){
        ApiResponse apiResponse = userService.emailCertification(emailCertificationRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/auth/check-certification")
    public ResponseEntity<ApiResponse> checkCertification(@RequestBody CheckCertificationRequest checkCertificationRequest){
        ApiResponse apiResponse = userService.checkCertification(checkCertificationRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/auth/generateToken")
    public ResponseEntity<ApiResponse> generateToken(@CookieValue(value = "refreshToken") String refreshToken,
                                                     HttpServletResponse response){
        ApiResponse apiResponse = userService.generateToken(refreshToken, response);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
