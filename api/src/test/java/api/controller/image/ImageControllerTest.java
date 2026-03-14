package api.controller.image;

import api.response.ApiDataResponse;
import api.service.image.ImageService;
import api.support.TestUsernameArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static exception.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        ImageController controller = new ImageController(imageService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/image 모임 이미지 목록 조회")
    void gatheringImage_성공() throws Exception {
        given(imageService.gatheringImage(eq(1L), eq(1), eq(10)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/1/image")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /image/{imageUrl} 이미지 조회 요청")
    void fetchImage_요청() throws Exception {
        // fetchImage returns Resource directly, not ResponseEntity
        // Testing that the endpoint is reachable
        given(imageService.fetchImage(eq("test.png"), any()))
                .willReturn(new org.springframework.core.io.ByteArrayResource(new byte[]{1, 2, 3}));

        mockMvc.perform(get("/image/test.png"))
                .andExpect(status().isOk());
    }
}
