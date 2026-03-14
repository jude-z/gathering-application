package api.controller.alarm;

import api.response.ApiStatusResponse;
import api.service.alarm.AlarmService;
import api.support.TestUsernameArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static exception.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlarmControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AlarmService alarmService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AlarmController controller = new AlarmController(alarmService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("PATCH /alarm/{id} 알람 확인 처리")
    void checkAlarm_성공() throws Exception {
        given(alarmService.checkAlarm(any(), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(patch("/alarm/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("DELETE /alarm/{id} 알람 삭제")
    void deleteAlarm_성공() throws Exception {
        given(alarmService.deleteAlarm(any(), eq(1L))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(delete("/alarm/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /alarm 알람 목록 조회")
    void alarmList_성공() throws Exception {
        given(alarmService.alarmList(eq(1), eq(10), eq(1L), eq(true))).willReturn(ApiStatusResponse.of(SUCCESS));

        mockMvc.perform(get("/alarm")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("checked", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
