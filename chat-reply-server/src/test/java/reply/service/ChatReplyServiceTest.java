package reply.service;

import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.chat.ReadStatus;
import entity.user.User;
import exception.CommonException;
import infra.kafka.event.Event;
import infra.kafka.event.EventType;
import infra.kafka.event.payload.ChatMessagePayload;
import infra.repository.chat.ChatMessageRepository;
import infra.repository.chat.ChatParticipantRepository;
import infra.repository.chat.ChatRoomRepository;
import infra.repository.chat.ReadStatusRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatReplyServiceTest {

    @InjectMocks
    private ChatReplyService chatReplyService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("handle null 이벤트면 아무 처리 안함")
    void handle_null이벤트() {
        chatReplyService.handle(null);

        verifyNoInteractions(userRepository, chatRoomRepository, chatParticipantRepository);
    }

    @Test
    @DisplayName("handle Redis 키 없으면 처리 중단")
    void handle_Redis키없음() {
        ReflectionTestUtils.setField(chatReplyService, "key", "chat-message");
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L).publisherId(1L).content("hello").createdAt(LocalDateTime.now()).build();
        payload.setEventId(100L);
        Event<ChatMessagePayload> event = (Event<ChatMessagePayload>) (Event) Event.builder()
                .type(EventType.CHAT_MESSAGE).payload(payload).build();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("chat-message:100")).willReturn(null);

        chatReplyService.handle(event);

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("handle 성공 - 메시지 저장 및 ReadStatus 생성")
    void handle_성공() {
        ReflectionTestUtils.setField(chatReplyService, "key", "chat-message");
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L).publisherId(1L).content("hello").createdAt(LocalDateTime.now()).build();
        payload.setEventId(100L);
        Event<ChatMessagePayload> event = (Event<ChatMessagePayload>) (Event) Event.builder()
                .type(EventType.CHAT_MESSAGE).payload(payload).build();

        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(2).build();
        ChatParticipant sender = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();
        ChatMessage savedMessage = ChatMessage.builder().chatRoom(chatRoom).chatParticipant(sender).build();
        ChatParticipant participant2 = ChatParticipant.builder().chatRoom(chatRoom).user(user).status(true).build();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("chat-message:100")).willReturn("exists");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.of(sender));
        given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(savedMessage);
        given(chatParticipantRepository.findAllByChatRoom(chatRoom)).willReturn(List.of(sender, participant2));

        chatReplyService.handle(event);

        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(readStatusRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("handle 사용자 없으면 CommonException")
    void handle_사용자없음() {
        ReflectionTestUtils.setField(chatReplyService, "key", "chat-message");
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L).publisherId(1L).content("hello").createdAt(LocalDateTime.now()).build();
        payload.setEventId(100L);
        Event<ChatMessagePayload> event = (Event<ChatMessagePayload>) (Event) Event.builder()
                .type(EventType.CHAT_MESSAGE).payload(payload).build();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("chat-message:100")).willReturn("exists");
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatReplyService.handle(event))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("handle 채팅방 없으면 CommonException")
    void handle_채팅방없음() {
        ReflectionTestUtils.setField(chatReplyService, "key", "chat-message");
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L).publisherId(1L).content("hello").createdAt(LocalDateTime.now()).build();
        payload.setEventId(100L);
        Event<ChatMessagePayload> event = (Event<ChatMessagePayload>) (Event) Event.builder()
                .type(EventType.CHAT_MESSAGE).payload(payload).build();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("chat-message:100")).willReturn("exists");
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatReplyService.handle(event))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("handle 참여자 아니면 CommonException")
    void handle_참여자아님() {
        ReflectionTestUtils.setField(chatReplyService, "key", "chat-message");
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .chatRoomId(1L).publisherId(1L).content("hello").createdAt(LocalDateTime.now()).build();
        payload.setEventId(100L);
        Event<ChatMessagePayload> event = (Event<ChatMessagePayload>) (Event) Event.builder()
                .type(EventType.CHAT_MESSAGE).payload(payload).build();

        User user = User.builder().username("u").password("p").nickname("n").build();
        ChatRoom chatRoom = ChatRoom.builder().title("t").description("d").count(1).build();
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("chat-message:100")).willReturn("exists");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatReplyService.handle(event))
                .isInstanceOf(CommonException.class);
    }
}
