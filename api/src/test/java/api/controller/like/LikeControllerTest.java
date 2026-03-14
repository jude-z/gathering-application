package api.controller.like;

import api.response.ApiStatusResponse;
import api.service.like.LikeService;
import api.support.TestUsernameArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static exception.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    private MockMvc mockMvc;
    @Mock
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        LikeController controller = new LikeController(likeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("PATCH /gathering/{gatheringId}/like 좋아요")
    void like_성공() throws Exception {
        given(likeService.like(eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(patch("/gathering/1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("PATCH /gathering/{gatheringId}/dislike 좋아요 취소")
    void dislike_성공() throws Exception {
        given(likeService.dislike(eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(patch("/gathering/1/dislike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
