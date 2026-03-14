package api.controller.meeting;

import api.response.ApiDataResponse;
import api.response.ApiStatusResponse;
import api.service.meeting.MeetingService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MeetingControllerTest {

    private MockMvc mockMvc;
    @Mock
    private MeetingService meetingService;

    @BeforeEach
    void setUp() {
        MeetingController controller = new MeetingController(meetingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("DELETE /gathering/{gatheringId}/meeting/{meetingId} 일정 삭제")
    void deleteMeeting_성공() throws Exception {
        given(meetingService.deleteMeeting(eq(1L), eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of(1L, SUCCESS));

        mockMvc.perform(delete("/gathering/1/meeting/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/meeting/{meetingId} 일정 상세 조회")
    void meetingDetail_성공() throws Exception {
        given(meetingService.meetingDetail(eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of("detail", SUCCESS));

        mockMvc.perform(get("/gathering/1/meeting/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/meetings 일정 목록 조회")
    void meetings_성공() throws Exception {
        given(meetingService.meetings(eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/1/meetings")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
