package reply.service;

import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.chat.ReadStatus;
import entity.user.User;
import exception.CommonException;
import exception.Status;
import infra.kafka.event.Event;
import infra.kafka.event.EventPayload;
import infra.kafka.event.payload.ChatMessagePayload;
import infra.repository.chat.ChatMessageRepository;
import infra.repository.chat.ChatParticipantRepository;
import infra.repository.chat.ChatRoomRepository;
import infra.repository.chat.ReadStatusRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reply.common.mapper.ReadStatusMapper;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ChatReplyService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String,String> redisTemplate;
    private final ReadStatusRepository readStatusRepository;
    @Value("{chat-message.key}")
    private String key;

    public void handle(Event<ChatMessagePayload> event) {
        if (event == null) return;

        ChatMessagePayload payload = event.getPayload();
        if (!validateEvent(payload)) return;

        ChatParticipant sender = findSender(payload);
        ChatMessage chatMessage = saveChatMessage(sender);
        saveReadStatuses(chatMessage, sender.getChatRoom());
    }

    private boolean validateEvent(ChatMessagePayload payload) {
        String chatKey = generateKey(payload);
        return StringUtils.hasText(redisTemplate.opsForValue().get(chatKey));
    }

    private ChatParticipant findSender(ChatMessagePayload payload) {
        User user = userRepository.findById(payload.getPublisherId())
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_USER));
        ChatRoom chatRoom = chatRoomRepository.findById(payload.getChatRoomId())
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_CHAT_ROOM));
        return chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom, user, true)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_CHAT_PARTICIPANT));
    }

    private ChatMessage saveChatMessage(ChatParticipant sender) {
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(sender.getChatRoom())
                .chatParticipant(sender)
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    private void saveReadStatuses(ChatMessage chatMessage, ChatRoom chatRoom) {
        List<ChatParticipant> participants = chatParticipantRepository.findAllByChatRoom(chatRoom);
        List<ReadStatus> readStatuses = participants.stream()
                .map(participant -> ReadStatusMapper.toReadStatus(participant, chatMessage))
                .toList();
        readStatusRepository.saveAll(readStatuses);
    }

    private String generateKey(EventPayload eventPayload){
        Long eventId = eventPayload.getEventId();
        return key + ":" + eventId;
    }

}
