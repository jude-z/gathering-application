package api.controller.attend;

import api.response.ApiStatusResponse;
import api.service.attend.AttendService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AttendControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AttendService attendService;

    @BeforeEach
    void setUp() {
        AttendController controller = new AttendController(attendService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("POST /gathering/{gatheringId}/meeting/{meetingId}/attend 참석")
    void addAttend_성공() throws Exception {
        given(attendService.addAttend(eq(1L), eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/gathering/1/meeting/1/attend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /gathering/{gatheringId}/meeting/{meetingId}/disAttend 참석 취소")
    void disAttend_성공() throws Exception {
        given(attendService.disAttend(eq(1L), eq(1L), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(post("/gathering/1/meeting/1/disAttend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
