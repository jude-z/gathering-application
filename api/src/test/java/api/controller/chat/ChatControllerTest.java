package api.controller.chat;

import api.response.ApiDataResponse;
import api.response.ApiStatusResponse;
import api.service.chat.ChatService;
import api.support.TestUsernameArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static api.requeset.chat.ChatRequestDto.*;
import static exception.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ChatController controller = new ChatController(chatService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new TestUsernameArgumentResolver(1L))
                .build();
    }

    @Test
    @DisplayName("GET /my/chats 내 채팅방 목록 조회")
    void fetchMyChatRooms_성공() throws Exception {
        given(chatService.fetchMyChatRooms(eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/my/chats")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/chats 모임 채팅방 목록 조회")
    void fetchChatRooms_성공() throws Exception {
        given(chatService.fetchChatRooms(eq(1L), eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/1/chats")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/able/chats 참여 가능 채팅방 조회")
    void fetchAbleChatRooms_성공() throws Exception {
        given(chatService.fetchAbleChatRooms(eq(1L), eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/1/able/chats")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /gathering/{gatheringId}/participate/chats 참여 중 채팅방 조회")
    void fetchParticipateChatRooms_성공() throws Exception {
        given(chatService.fetchParticipateChatRooms(eq(1L), eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/gathering/1/participate/chats")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /gathering/{gatheringId}/chat 채팅방 생성")
    void addChatRoom_성공() throws Exception {
        AddChatRequest request = AddChatRequest.builder()
                .title("title").description("desc").build();
        given(chatService.addChatRoom(eq(1L), any(AddChatRequest.class), eq(1L)))
                .willReturn(ApiDataResponse.of(1L, SUCCESS));

        mockMvc.perform(post("/gathering/1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /chat/attend/{chatId} 채팅방 참여")
    void attendChat_성공() throws Exception {
        given(chatService.attendChat(eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of(1L, SUCCESS));

        mockMvc.perform(post("/chat/attend/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /chat/disAttend/{chatId} 채팅방 나가기")
    void leaveChat_성공() throws Exception {
        given(chatService.leaveChat(eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of(1L, SUCCESS));

        mockMvc.perform(post("/chat/disAttend/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /messages/{chatId} 메시지 조회")
    void fetchMessages_성공() throws Exception {
        given(chatService.fetchUnReadMessages(eq(1L), eq(1), eq(10), eq(1L)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/messages/1")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("POST /chat/{chatId} 메시지 읽음 처리")
    void readChatMessage_성공() throws Exception {
        given(chatService.readChatMessage(eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of(1L, SUCCESS));

        mockMvc.perform(post("/chat/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /chat/{chatId} 채팅방 상세 조회")
    void fetchChat_성공() throws Exception {
        given(chatService.fetchChat(eq(1L), eq(1L)))
                .willReturn(ApiDataResponse.of("chat", SUCCESS));

        mockMvc.perform(get("/chat/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }

    @Test
    @DisplayName("GET /chat/participant/{chatId} 참여자 목록 조회")
    void fetchParticipant_성공() throws Exception {
        given(chatService.fetchParticipant(eq(1L), eq(1L), eq(1), eq(10)))
                .willReturn(ApiDataResponse.of(List.of(), SUCCESS));

        mockMvc.perform(get("/chat/participant/1")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SU"));
    }
}
