package api.controller.enrollment;

import api.response.ApiStatusResponse;
import api.service.enrollment.EnrollmentService;
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
class EnrollmentControllerTest {

    private MockMvc mockMvc;
    @Mock
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        EnrollmentController controller = new EnrollmentController(enrollmentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("PATCH /gathering/{gatheringId}/participate 모임 가입")
    void enrollGathering_성공() throws Exception {
        given(enrollmentService.enrollGathering(eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(patch("/gathering/1/participate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("PATCH /gathering/{gatheringId}/disParticipate 모임 탈퇴")
    void disEnrollGathering_성공() throws Exception {
        given(enrollmentService.disEnrollGathering(eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(patch("/gathering/1/disParticipate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("PATCH /gathering/{gatheringId}/permit/{enrollmentId} 가입 승인")
    void permit_성공() throws Exception {
        given(enrollmentService.permit(eq(1L), eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(patch("/gathering/1/permit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
