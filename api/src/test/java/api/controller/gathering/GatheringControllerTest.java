package api.controller.gathering;

import api.response.ApiDataResponse;
import api.response.ApiStatusResponse;
import api.service.gathering.GatheringService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GatheringControllerTest {

    private MockMvc mockMvc;
    @Mock
    private GatheringService gatheringService;

    @BeforeEach
    void setUp() {
        GatheringController controller = new GatheringController(gatheringService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId} 모임 상세 조회")
    void gatheringDetail_성공() throws Exception {
        given(gatheringService.gatheringDetail(eq(1L)))
                .willReturn(ApiDataResponse.of("detail", SUCCESS));

        mockMvc.perform(get("/gathering/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/participated/{gatheringId} 참여자 목록 조회")
    void participated_성공() throws Exception {
        given(gatheringService.participated(eq(1L), eq(1), eq(10)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/participated/1")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gatherings 전체 모임 목록 조회")
    void gatherings_성공() throws Exception {
        given(gatheringService.gatherings())
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gatherings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering 카테고리별 모임 조회")
    void gatheringCategory_성공() throws Exception {
        given(gatheringService.gatheringCategory(eq("sports"), eq(1), eq(10)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering")
                        .param("category", "sports")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /gatherings/like 좋아요한 모임 조회")
    void gatheringsLike_성공() throws Exception {
        given(gatheringService.gatheringsLike(eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(post("/gatherings/like")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
