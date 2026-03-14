package api.service.chat;

import api.response.ApiResponse;
import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.gathering.Gathering;
import entity.user.User;
import exception.CommonException;
import infra.repository.chat.*;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.chat.*;
import infra.repository.gathering.GatheringRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static api.requeset.chat.ChatRequestDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QueryDslChatRepository queryDslChatRepository;
    @Mock
    private JdbcChatRepository jdbcChatRepository;

    @Test
    @DisplayName("fetchChatRooms 성공")
    void fetchChatRooms_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        QueryDslPageResponse<ChatRoomProjection> pageResponse = QueryDslPageResponse.<ChatRoomProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslChatRepository.fetchChatRooms(any(), eq(1L), eq(1L))).willReturn(pageResponse);

        ApiResponse response = chatService.fetchChatRooms(1L, 1, 10, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchChatRooms 사용자 없으면 CommonException")
    void fetchChatRooms_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchChatRooms(1L, 1, 10, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchChatRooms 모임 없으면 CommonException")
    void fetchChatRooms_모임없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(gatheringRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchChatRooms(1L, 1, 10, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchMyChatRooms 성공")
    void fetchMyChatRooms_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        QueryDslPageResponse<MyChatRoomProjection> pageResponse = QueryDslPageResponse.<MyChatRoomProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslChatRepository.fetchMyChatRooms(any(), eq(1L))).willReturn(pageResponse);

        ApiResponse response = chatService.fetchMyChatRooms(1, 10, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchMyChatRooms 사용자 없으면 CommonException")
    void fetchMyChatRooms_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchMyChatRooms(1, 10, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchAbleChatRooms 성공")
    void fetchAbleChatRooms_성공() {
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        QueryDslPageResponse<AbleChatRoomProjection> pageResponse = QueryDslPageResponse.<AbleChatRoomProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslChatRepository.fetchAbleChatRooms(any(), eq(1L), eq(1L))).willReturn(pageResponse);

        ApiResponse response = chatService.fetchAbleChatRooms(1L, 1, 10, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchAbleChatRooms 모임 없으면 CommonException")
    void fetchAbleChatRooms_모임없음() {
        given(gatheringRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchAbleChatRooms(1L, 1, 10, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchParticipateChatRooms 성공")
    void fetchParticipateChatRooms_성공() {
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        QueryDslPageResponse<ParticipateChatRoomProjection> pageResponse = QueryDslPageResponse.<ParticipateChatRoomProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslChatRepository.fetchParticipateChatRooms(any(), eq(1L), eq(1L))).willReturn(pageResponse);

        ApiResponse response = chatService.fetchParticipateChatRooms(1L, 1, 10, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("addChatRoom 성공")
    void addChatRoom_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        AddChatRequest request = AddChatRequest.builder().title("chat").description("desc").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));

        ApiResponse response = chatService.addChatRoom(1L, request, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(chatParticipantRepository).save(any(ChatParticipant.class));
    }

    @Test
    @DisplayName("addChatRoom 사용자 없으면 CommonException")
    void addChatRoom_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.addChatRoom(1L, AddChatRequest.builder().title("t").description("d").build(), 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addChatRoom 모임 없으면 CommonException")
    void addChatRoom_모임없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(gatheringRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.addChatRoom(1L, AddChatRequest.builder().title("t").description("d").build(), 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("attendChat 새 참여자 등록")
    void attendChat_새참여자() {
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, false)).willReturn(Optional.empty());

        ApiResponse response = chatService.attendChat(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(chatParticipantRepository).save(any(ChatParticipant.class));
    }

    @Test
    @DisplayName("attendChat 기존 참여자 상태 변경")
    void attendChat_기존참여자() {
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatParticipant participant = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(false).build();
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, false)).willReturn(Optional.of(participant));

        ApiResponse response = chatService.attendChat(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("attendChat 채팅방 없으면 CommonException")
    void attendChat_채팅방없음() {
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.attendChat(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("leaveChat 성공")
    void leaveChat_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(2).build();
        ChatParticipant participant = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.of(participant));

        ApiResponse response = chatService.leaveChat(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("leaveChat 참여자 없으면 CommonException")
    void leaveChat_참여자없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.leaveChat(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchUnReadMessages 성공")
    void fetchUnReadMessages_성공() {
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatParticipant participant = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.of(participant));
        QueryDslPageResponse<ChatMessageProjection> pageResponse = QueryDslPageResponse.<ChatMessageProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslChatRepository.fetchUnReadMessages(any(), eq(1L), eq(1L))).willReturn(pageResponse);

        ApiResponse response = chatService.fetchUnReadMessages(1L, 1, 10, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchUnReadMessages 채팅방 없으면 CommonException")
    void fetchUnReadMessages_채팅방없음() {
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchUnReadMessages(1L, 1, 10, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("readChatMessage 성공")
    void readChatMessage_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        ChatParticipant participant = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();
        org.springframework.test.util.ReflectionTestUtils.setField(participant, "id", 10L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.of(participant));
        given(chatMessageRepository.findChatMessageByChatRoom(chatRoom)).willReturn(List.of());

        ApiResponse response = chatService.readChatMessage(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("isRoomParticipant 참여자면 true 반환")
    void isRoomParticipant_참여자() {
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatParticipant participant = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.of(participant));

        boolean result = chatService.isRoomParticipant(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRoomParticipant 참여자 아니면 CommonException")
    void isRoomParticipant_비참여자() {
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.isRoomParticipant(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchChat 성공")
    void fetchChat_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(queryDslChatRepository.fetchChatRoomById(1L)).willReturn(Optional.of(chatRoom));

        ApiResponse response = chatService.fetchChat(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchChat 채팅방 없으면 CommonException")
    void fetchChat_채팅방없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(queryDslChatRepository.fetchChatRoomById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchChat(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchParticipant 성공")
    void fetchParticipant_성공() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(ChatRoom.builder().title("t").description("d").count(1).build()));
        QueryDslPageResponse<ParticipantProjection> pageResponse = QueryDslPageResponse.<ParticipantProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslChatRepository.fetchParticipant(any(), eq(1L), eq(1L))).willReturn(pageResponse);

        ApiResponse response = chatService.fetchParticipant(1L, 1L, 1, 10);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchParticipant 사용자 없으면 CommonException")
    void fetchParticipant_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchParticipant(1L, 1L, 1, 10))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchParticipant 채팅방 없으면 CommonException")
    void fetchParticipant_채팅방없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.fetchParticipant(1L, 1L, 1, 10))
                .isInstanceOf(CommonException.class);
    }
}
