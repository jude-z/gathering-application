package api.controller.user;

import api.response.ApiDataResponse;
import api.response.ApiStatusResponse;
import api.service.user.UserService;
import api.support.TestUsernameArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static api.requeset.user.UserRequestDto.*;
import static exception.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("POST /auth/id-check 아이디 중복 체크")
    void idCheck_성공() throws Exception {
        IdCheckRequest request = IdCheckRequest.builder().username("newUser").build();
        given(userService.idCheck(any(IdCheckRequest.class)))
                .willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/auth/id-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /auth/nickname-check 닉네임 중복 체크")
    void nicknameCheck_성공() throws Exception {
        NicknameCheckRequest request = NicknameCheckRequest.builder().nickname("newNick").build();
        given(userService.nicknameCheck(any(NicknameCheckRequest.class)))
                .willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/auth/nickname-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /auth/user/{userId} 사용자 정보 조회")
    void fetchUser_성공() throws Exception {
        given(userService.fetchUser(eq(1L)))
                .willReturn(ApiDataResponse.of("user", SUCCESS));

        mockMvc.perform(get("/auth/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /auth/sign-in 로그인")
    void signIn_성공() throws Exception {
        SignInRequest request = SignInRequest.builder().username("user").password("pass").build();
        given(userService.signIn(any(SignInRequest.class), any()))
                .willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /auth/email-certification 이메일 인증 요청")
    void emailCertification_성공() throws Exception {
        EmailCertificationRequest request = EmailCertificationRequest.builder()
                .clientId("client").email("test@test.com").build();
        given(userService.emailCertification(any(EmailCertificationRequest.class)))
                .willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/auth/email-certification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /auth/check-certification 인증 코드 확인")
    void checkCertification_성공() throws Exception {
        CheckCertificationRequest request = CheckCertificationRequest.builder()
                .certification("1234").email("test@test.com").build();
        given(userService.checkCertification(any(CheckCertificationRequest.class)))
                .willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/auth/check-certification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
