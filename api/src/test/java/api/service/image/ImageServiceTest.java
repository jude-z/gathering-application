package api.service.image;

import api.response.ApiResponse;
import entity.image.Image;
import exception.CommonException;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.image.ImageRepository;
import infra.repository.image.QueryDslImageRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;
    @Mock
    private ImageDownloadService imageDownloadService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private QueryDslImageRepository queryDslImageRepository;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "url", "http://localhost:8080");
    }

    @Test
    @DisplayName("fetchImage 성공 시 리소스 반환")
    void fetchImage_성공() throws IOException {
        Image image = Image.builder().url("test.png").contentType("image/png").build();
        Resource resource = new ByteArrayResource(new byte[]{1, 2, 3});
        given(imageRepository.findByUrl("test.png")).willReturn(Optional.of(image));
        given(imageDownloadService.getFileByteArrayFromS3("test.png")).willReturn(resource);

        Resource result = imageService.fetchImage("test.png", response);

        assertThat(result).isEqualTo(resource);
        verify(response).setContentType("image/png");
    }

    @Test
    @DisplayName("fetchImage 이미지 없으면 CommonException")
    void fetchImage_이미지없음() {
        given(imageRepository.findByUrl("notfound.png")).willReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.fetchImage("notfound.png", response))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("gatheringImage 성공 시 URL 목록 반환")
    void gatheringImage_성공() {
        QueryDslPageResponse<String> pageResponse = QueryDslPageResponse.<String>builder()
                .content(List.of("img1.png", "img2.png")).totalCount(2).pageNum(1).pageSize(10).totalPage(1).isEmpty(false).build();
        given(queryDslImageRepository.gatheringImage(anyLong(), any())).willReturn(pageResponse);

        ApiResponse result = imageService.gatheringImage(1L, 1, 10);

        assertThat(result.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("gatheringImage 빈 목록 시에도 성공 응답")
    void gatheringImage_빈목록() {
        QueryDslPageResponse<String> pageResponse = QueryDslPageResponse.<String>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslImageRepository.gatheringImage(anyLong(), any())).willReturn(pageResponse);

        ApiResponse result = imageService.gatheringImage(1L, 1, 10);

        assertThat(result.getCode()).isEqualTo("SU");
    }
}
