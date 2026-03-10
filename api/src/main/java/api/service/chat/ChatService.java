package api.service.chat;

import api.response.ApiDataResponse;
import api.response.ApiResponse;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.gathering.Gathering;
import entity.user.User;
import exception.CommonException;
import infra.dto.querydsl.chat.*;
import jakarta.transaction.Transactional;
import infra.repository.chat.JdbcChatRepository;
import infra.repository.chat.ChatMessageRepository;
import infra.repository.chat.ChatParticipantRepository;
import infra.repository.chat.ChatRoomRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import infra.repository.chat.QueryDslChatRepository;
import util.PageCalculator;

import java.util.List;
import java.util.Optional;

import static api.requeset.chat.ChatRequestDto.*;
import static exception.Status.*;


@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final GatheringRepository gatheringRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final QueryDslChatRepository queryDslChatRepository;
    private final JdbcChatRepository jdbcChatRepository;
    @Value("${path}")
    private String path;


    public ApiResponse fetchChatRooms(Long gatheringId, Integer pageNum, Integer pageSize, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        gatheringRepository.findById(gatheringId)
                .orElseThrow(()->new CommonException(NOT_FOUND_GATHERING));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<ChatRoomProjection> queryDslPageResponse = queryDslChatRepository.fetchChatRooms(pageableInfo,userId,gatheringId);
        return ApiDataResponse.of(queryDslPageResponse, SUCCESS);
    }

    public ApiResponse fetchMyChatRooms(Integer pageNum, Integer pageSize, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<MyChatRoomProjection> queryDslPageResponse = queryDslChatRepository.fetchMyChatRooms(pageableInfo,userId);
        return ApiDataResponse.of(queryDslPageResponse, SUCCESS);
    }

    public ApiResponse fetchAbleChatRooms(Long gatheringId, Integer pageNum, Integer pageSize, Long userId) {
        gatheringRepository.findById(gatheringId)
                .orElseThrow(()->new CommonException(NOT_FOUND_GATHERING));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<AbleChatRoomProjection> queryDslPageResponse = queryDslChatRepository.fetchAbleChatRooms(pageableInfo,userId,gatheringId);
        return ApiDataResponse.of(queryDslPageResponse, SUCCESS);
    }

    public ApiResponse fetchParticipateChatRooms(Long gatheringId, Integer pageNum, Integer pageSize, Long userId) {
        gatheringRepository.findById(gatheringId)
                .orElseThrow(()->new CommonException(NOT_FOUND_GATHERING));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<ParticipateChatRoomProjection> queryDslPageResponse = queryDslChatRepository.fetchParticipateChatRooms(pageableInfo,userId,gatheringId);
        return ApiDataResponse.of(queryDslPageResponse, SUCCESS);
    }

    public ApiResponse addChatRoom(Long gatheringId, AddChatRequest addChatRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
        ChatRoom chatRoom = AddChatRequest.toChatRoom(addChatRequest,user,gathering);
        ChatParticipant chatParticipant = ChatParticipant.of(chatRoom, user,false);
        chatRoomRepository.save(chatRoom);
        chatParticipantRepository.save(chatParticipant);
        return ApiDataResponse.of(chatRoom.getId(), SUCCESS);
    }

    public ApiResponse attendChat(Long chatId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CHAT_ROOM));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        Optional<ChatParticipant> optionalChatParticipant = chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,false);
        if(optionalChatParticipant.isEmpty()){
            chatParticipantRepository.save(ChatParticipant.of(chatRoom,user,true));
            chatRoom.changeCount(chatRoom.getCount()+1);
        }
        if(optionalChatParticipant.isPresent()) optionalChatParticipant.get().changeStatus(true);
        return ApiDataResponse.of(chatId, SUCCESS);
    }

    public ApiResponse leaveChat(Long chatId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CHAT_ROOM));
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()-> new CommonException(NOT_FOUND_CHAT_PARTICIPANT));
        chatParticipant.changeStatus(false);
        return ApiDataResponse.of(chatId, SUCCESS);
    }

    public ApiResponse fetchUnReadMessages(Long chatId, Integer pageNum, Integer pageSize, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CHAT_ROOM));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()->new CommonException(NOT_FOUND_CHAT_PARTICIPANT));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<ChatMessageProjection> queryDslPageResponse = queryDslChatRepository.fetchUnReadMessages(pageableInfo,chatId,userId);
        return ApiDataResponse.of(queryDslPageResponse, SUCCESS);
    }

    public ApiResponse readChatMessage(Long chatId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CHAT_ROOM));
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()-> new CommonException(NOT_FOUND_CHAT_PARTICIPANT));
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessageByChatRoom(chatRoom);
        Long chatParticipantId = chatParticipant.getId();
        List<Long> chatMessagesId = chatMessages.stream().map(ChatMessage::getId).toList();
        jdbcChatRepository.readChatMessage(chatParticipantId,chatMessagesId);
        return ApiDataResponse.of(chatId, SUCCESS);
    }

    public boolean isRoomParticipant(Long userId, long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CHAT_ROOM));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()->new CommonException(NOT_FOUND_CHAT_PARTICIPANT));
        return true;
    }

    public ApiResponse fetchChat(Long chatId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        ChatRoom chatRoom = queryDslChatRepository.fetchChatRoomById(chatId)
                .orElseThrow(()-> new CommonException(NOT_FOUND_CHAT_ROOM));
        return ApiDataResponse.of(chatRoom, SUCCESS);
    }

    public ApiResponse fetchParticipant(Long chatId, Long userId, Integer pageNum, Integer pageSize) {
        userRepository.findById(userId)
                .orElseThrow(()->new CommonException(NOT_FOUND_USER));
        chatRoomRepository.findById(chatId)
                .orElseThrow(()->new CommonException(NOT_FOUND_CHAT_ROOM));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<ParticipantProjection> queryDslPageResponse = queryDslChatRepository.fetchParticipant(pageableInfo,chatId,userId);
        return ApiDataResponse.of(queryDslPageResponse, SUCCESS);
    }
}
