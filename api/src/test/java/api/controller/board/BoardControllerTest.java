package api.controller.board;

import api.response.ApiDataResponse;
import api.service.board.BoardService;
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
class BoardControllerTest {

    private MockMvc mockMvc;
    @Mock
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        BoardController controller = new BoardController(boardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/board/{boardId} 게시글 상세 조회")
    void fetchBoard_성공() throws Exception {
        given(boardService.fetchBoard(eq(1L), eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of("board", SUCCESS));

        mockMvc.perform(get("/gathering/1/board/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/boards 게시글 목록 조회")
    void fetchBoards_성공() throws Exception {
        given(boardService.fetchBoards(eq(1L), eq(1), eq(10)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/1/boards")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
