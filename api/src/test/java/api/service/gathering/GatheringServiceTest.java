package api.service.gathering;

import api.response.ApiResponse;
import api.service.image.ImageUploadService;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.user.User;
import exception.CommonException;
import infra.repository.category.CategoryRepository;
import infra.repository.category.QueryDslCategoryRepository;
import infra.repository.dto.jdbc.gathering.GatheringDetailProjection;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.gathering.GatheringsProjection;
import infra.repository.dto.querydsl.gathering.ParticipatedProjection;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.gathering.JdbcGatheringRepository;
import infra.repository.gathering.QueryDslGatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GatheringServiceTest {

    @InjectMocks
    private GatheringService gatheringService;
    @Mock private GatheringRepository gatheringRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private QueryDslCategoryRepository queryDslCategoryRepository;
    @Mock private QueryDslGatheringRepository queryDslGatheringRepository;
    @Mock private JdbcGatheringRepository jdbcGatheringRepository;
    @Mock private ImageUploadService imageUploadService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gatheringService, "path", "http://localhost:8080");
    }

    @Test
    @DisplayName("addGathering 사용자 없으면 CommonException")
    void addGathering_사용자없음() {
        var request = api.requeset.gathering.GatheringRequestDto.AddGatheringRequest.builder()
                .title("t").content("c").category("category1").build();
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> gatheringService.addGathering(request, null, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addGathering 카테고리 없으면 CommonException")
    void addGathering_카테고리없음() {
        var request = api.requeset.gathering.GatheringRequestDto.AddGatheringRequest.builder()
                .title("t").content("c").category("없는카테고리XYZ").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> gatheringService.addGathering(request, null, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addGathering 성공 - 이미지 없는 경우")
    void addGathering_성공_이미지없음() throws IOException {
        var request = api.requeset.gathering.GatheringRequestDto.AddGatheringRequest.builder()
                .title("t").content("c").category("category1").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        ApiResponse response = gatheringService.addGathering(request, null, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(gatheringRepository).save(any(Gathering.class));
    }

    @Test
    @DisplayName("updateGathering 사용자 없으면 CommonException")
    void updateGathering_사용자없음() {
        var request = api.requeset.gathering.GatheringRequestDto.UpdateGatheringRequest.builder()
                .title("t").content("c").category("category1").build();
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> gatheringService.updateGathering(request, null, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("updateGathering 카테고리 없으면 CommonException")
    void updateGathering_카테고리없음() {
        var request = api.requeset.gathering.GatheringRequestDto.UpdateGatheringRequest.builder()
                .title("t").content("c").category("category1").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslCategoryRepository.findBy(1L, "category1")).willReturn(Optional.empty());

        assertThatThrownBy(() -> gatheringService.updateGathering(request, null, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("updateGathering 모임 없으면 CommonException")
    void updateGathering_모임없음() {
        var request = api.requeset.gathering.GatheringRequestDto.UpdateGatheringRequest.builder()
                .title("t").content("c").category("category1").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        Category category = Category.builder().name("category1").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslCategoryRepository.findBy(1L, "category1")).willReturn(Optional.of(category));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> gatheringService.updateGathering(request, null, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("updateGathering 권한 없으면 CommonException")
    void updateGathering_권한없음() {
        var request = api.requeset.gathering.GatheringRequestDto.UpdateGatheringRequest.builder()
                .title("t").content("c").category("category1").build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        User otherUser = User.builder().username("o").password("p").nickname("o").build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);
        Category category = Category.builder().name("category1").build();
        Gathering gathering = Gathering.builder().title("t").content("c").createBy(otherUser).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslCategoryRepository.findBy(1L, "category1")).willReturn(Optional.of(category));
        given(queryDslGatheringRepository.findGatheringFetchCreatedBy(1L)).willReturn(Optional.of(gathering));

        assertThatThrownBy(() -> gatheringService.updateGathering(request, null, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("gatheringDetail 성공")
    void gatheringDetail_성공() {
        GatheringDetailProjection projection = GatheringDetailProjection.builder()
                .title("title").content("content").category("category1")
                .createdBy("creator").createdByUrl("/img.png").url("/img.png")
                .count(5).build();
        given(jdbcGatheringRepository.gatheringDetail(1L)).willReturn(List.of(projection));

        ApiResponse response = gatheringService.gatheringDetail(1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("gatheringDetail 모임 없으면 CommonException")
    void gatheringDetail_모임없음() {
        given(jdbcGatheringRepository.gatheringDetail(1L)).willReturn(List.of());

        assertThatThrownBy(() -> gatheringService.gatheringDetail(1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("gatheringCategory 성공")
    void gatheringCategory_성공() {
        QueryDslPageResponse<GatheringsProjection> pageResponse = QueryDslPageResponse.<GatheringsProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslGatheringRepository.gatheringsCategory(any(), eq("category1"))).willReturn(pageResponse);

        ApiResponse response = gatheringService.gatheringCategory("category1", 1, 10);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("gatheringsLike 사용자 없으면 CommonException")
    void gatheringsLike_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> gatheringService.gatheringsLike(1, 10, 1L))
                .isInstanceOf(CommonException.class);
    }
}
