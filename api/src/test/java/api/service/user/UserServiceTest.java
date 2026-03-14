package api.service.user;

import api.response.ApiResponse;
import api.service.image.ImageUploadService;
import entity.image.Image;
import entity.user.User;
import exception.CommonException;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.image.ImageRepository;
import infra.repository.user.QueryDslUserRepository;
import infra.repository.user.UserRepository;
import infra.repository.certification.QueryDslCertificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import util.ImageUrlConverter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static api.requeset.user.UserRequestDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private QueryDslUserRepository queryDslUserRepository;
    @Mock
    private QueryDslCertificationRepository queryDslCertificationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ImageUploadService imageUploadService;
    @Mock
    private ImageUrlConverter imageUrlConverter;

    @Test
    @DisplayName("idCheck 성공 - 사용 가능한 아이디")
    void idCheck_성공() {
        IdCheckRequest request = IdCheckRequest.builder().username("newUser").build();
        given(userRepository.existsByUsername("newUser")).willReturn(false);

        ApiResponse response = userService.idCheck(request);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("idCheck 이미 존재하는 아이디면 CommonException")
    void idCheck_이미존재() {
        IdCheckRequest request = IdCheckRequest.builder().username("existUser").build();
        given(userRepository.existsByUsername("existUser")).willReturn(true);

        assertThatThrownBy(() -> userService.idCheck(request))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("nicknameCheck 성공")
    void nicknameCheck_성공() {
        NicknameCheckRequest request = NicknameCheckRequest.builder().nickname("newNick").build();
        given(userRepository.existsByNickname("newNick")).willReturn(false);

        ApiResponse response = userService.nicknameCheck(request);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("nicknameCheck 이미 존재하면 CommonException")
    void nicknameCheck_이미존재() {
        NicknameCheckRequest request = NicknameCheckRequest.builder().nickname("existNick").build();
        given(userRepository.existsByNickname("existNick")).willReturn(true);

        assertThatThrownBy(() -> userService.nicknameCheck(request))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("signUp 성공 - 이미지 포함")
    void signUp_성공() throws IOException {
        SignUpRequest request = SignUpRequest.builder()
                .username("u").password("p").email("e@e.com").address("a").age(20).hobby("h").nickname("n").build();
        MultipartFile file = mock(MultipartFile.class);
        given(file.isEmpty()).willReturn(false);
        given(file.getContentType()).willReturn("image/png");
        given(imageUploadService.upload(file)).willReturn("uploaded.png");
        given(passwordEncoder.encode("p")).willReturn("encoded");

        ApiResponse response = userService.signUp(request, file);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(imageRepository).save(any(Image.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("update 성공")
    void update_성공() throws IOException {
        UpdateRequest request = UpdateRequest.builder()
                .email("e@e.com").address("a").age(20).hobby("h").nickname("n").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        ApiResponse response = userService.update(request, 1L, null, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("update 권한 없으면 CommonException")
    void update_권한없음() {
        UpdateRequest request = UpdateRequest.builder()
                .email("e@e.com").address("a").age(20).hobby("h").nickname("n").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.update(request, 1L, null, 2L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("signIn 성공")
    void signIn_성공() {
        SignInRequest request = SignInRequest.builder().username("u").password("p").build();
        User user = User.builder().username("u").password("encoded").nickname("n").build();
        given(queryDslUserRepository.findByUsername("u")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("p", "encoded")).willReturn(true);

        ApiResponse response = userService.signIn(request, null);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("signIn 비밀번호 불일치 시 CommonException")
    void signIn_비밀번호불일치() {
        SignInRequest request = SignInRequest.builder().username("u").password("wrong").build();
        User user = User.builder().username("u").password("encoded").nickname("n").build();
        given(queryDslUserRepository.findByUsername("u")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

        assertThatThrownBy(() -> userService.signIn(request, null))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("signIn 사용자 없으면 CommonException")
    void signIn_사용자없음() {
        SignInRequest request = SignInRequest.builder().username("unknown").password("p").build();
        given(queryDslUserRepository.findByUsername("unknown")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.signIn(request, null))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchUser 성공")
    void fetchUser_성공() {
        Image image = Image.builder().url("img.png").contentType("image/png").build();
        User user = User.builder().username("u").password("p").nickname("n").email("e").address("a").age(20).hobby("h").profileImage(image).build();
        given(queryDslUserRepository.findByIdFetchImage(1L)).willReturn(Optional.of(user));
        given(imageUrlConverter.convert("img.png")).willReturn("http://localhost/img.png");

        ApiResponse response = userService.fetchUser(1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchUser 사용자 없으면 CommonException")
    void fetchUser_사용자없음() {
        given(queryDslUserRepository.findByIdFetchImage(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.fetchUser(1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("emailCertification 이메일 중복 시 CommonException")
    void emailCertification_이메일중복() {
        EmailCertificationRequest request = EmailCertificationRequest.builder().clientId("c").email("e@e.com").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        QueryDslPageResponse<User> pageResponse = QueryDslPageResponse.<User>builder()
                .content(List.of(user)).totalCount(1).pageNum(1).pageSize(10).totalPage(1).isEmpty(false).build();
        given(queryDslUserRepository.findByEmail(any(), eq("e@e.com"))).willReturn(pageResponse);

        assertThatThrownBy(() -> userService.emailCertification(request))
                .isInstanceOf(CommonException.class);
    }
}
