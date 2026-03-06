package api.service.chat;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final GatheringRepository gatheringRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    @Value("${server.url}")
    private String url;


    public ChatRoomResponse fetchChatRooms(Long gatheringId, Integer pageNum, Integer pageSize,Long userId) {

        userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        gatheringRepository.findById(gatheringId)
                .orElseThrow(()->new NotFoundGatheringException("no exist Gathering!!"));
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<ChatRoomElement> page = chatRoomRepository.fetchChatRooms(pageRequest,userId,gatheringId);
        List<ChatRoomElement> content = page.getContent();
        boolean hasNext = page.hasNext();
        return ChatRoomResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    public MyChatRoomResponse fetchMyChatRooms(Integer pageNum, Integer pageSize,Long userId) {
        userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<MyChatRoomElement> page = chatRoomRepository.fetchMyChatRooms(pageRequest,userId);
        List<MyChatRoomElement> content = page.getContent();
        boolean hasNext = page.hasNext();
        return MyChatRoomResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }


    public AbleChatRoomResponse fetchAbleChatRooms(Long gatheringId, Integer pageNum, Integer pageSize,Long userId) {
        gatheringRepository.findById(gatheringId)
                .orElseThrow(()->new NotFoundGatheringException("no exist Gathering!!"));
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<AbleChatRoomElement> page = chatRoomRepository.fetchAbleChatRooms(pageRequest, userId,gatheringId);
        List<AbleChatRoomElement> content = page.getContent();
        boolean hasNext = page.hasNext();
        return AbleChatRoomResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    public ParticipateChatRoomResponse fetchParticipateChatRooms(Long gatheringId, Integer pageNum, Integer pageSize, Long userId) {
        gatheringRepository.findById(gatheringId)
                .orElseThrow(()->new NotFoundGatheringException("no exist Gathering!!"));
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<ParticipateChatRoomElement> page = chatRoomRepository.fetchParticipateChatRooms(pageRequest, userId,gatheringId);
        List<ParticipateChatRoomElement> content = page.getContent();
        boolean hasNext = page.hasNext();
        return ParticipateChatRoomResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    public AddChatRoomResponse addChatRoom(Long gatheringId,AddChatRequest addChatRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
        ChatRoom chatRoom = AddChatRequest.toChatRoom(addChatRequest,user,gathering);
        ChatParticipant chatParticipant = ChatParticipant.of(chatRoom, user,false);
        chatRoomRepository.save(chatRoom);
        chatParticipantRepository.save(chatParticipant);
        return AddChatRoomResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE, chatRoom.getId());
    }

    public AttendChatResponse attendChat(Long chatId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundChatRoomException("no exist ChatRoom!!"));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        Optional<ChatParticipant> optionalChatParticipant = chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,false);
        if(optionalChatParticipant.isEmpty()){
            chatParticipantRepository.save(ChatParticipant.of(chatRoom,user,true));
            chatRoom.changeCount(chatRoom.getCount()+1);
        }
        if(optionalChatParticipant.isPresent()) optionalChatParticipant.get().changeStatus(true);
        return AttendChatResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public LeaveChatResponse leaveChat(Long chatId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundChatRoomException("no exist ChatRoom!!"));
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()-> new NotFoundChatParticipantException("no exist ChatParticipant!!"));
        chatParticipant.changeStatus(false);
        return LeaveChatResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public ChatMessagesResponse fetchUnReadMessages(Long chatId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundChatRoomException("no exist ChatRoom!!"));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()->new NotFoundChatParticipantException("no exist ChatParticipant!!"));
        List<ChatMessageElement> content = chatMessageRepository.fetchUnReadMessages(chatId,userId);
        return ChatMessagesResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE, content);
    }

    public ReadChatMessageResponse readChatMessage(Long chatId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundChatRoomException("no exist ChatRoom!!"));
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()-> new NotFoundChatParticipantException("no exist ChatParticipant!!"));
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessageByChatRoom(chatRoom);
        Long chatParticipantId = chatParticipant.getId();
        List<Long> chatMessagesId = chatMessages.stream().map(c -> c.getId()).toList();
        readStatusRepository.readChatMessage(chatParticipantId,chatMessagesId);
        return ReadChatMessageResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public boolean isRoomParticipant(Long userId, long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundChatRoomException("no exist ChatRoom!!"));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        chatParticipantRepository.findByChatRoomAndUserAndStatus(chatRoom,user,true)
                .orElseThrow(()->new NotFoundChatParticipantException("no exist ChatParticipant!!"));

        return true;
    }

    public FetchChatRoomResponse fetchChat(Long chatId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        ChatRoom chatRoom = chatRoomRepository.fetchChatRoomById(chatId)
                .orElseThrow(()-> new NotFoundChatRoomException("Not Found ChatRoom"));
        return of(SUCCESS_CODE,SUCCESS_MESSAGE,chatRoom);
    }

    public FetchParticipantResponse fetchParticipant(Long chatId, Long userId, Integer pageNum, Integer pageSize) {
        userRepository.findById(userId)
                .orElseThrow(()->new NotFoundUserException("no exist User!!"));
        chatRoomRepository.findById(chatId)
                .orElseThrow(()->new NotFoundChatRoomException("Not Found ChatRoom!!"));
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<ParticipantElement> page = chatParticipantRepository.fetchParticipant(chatId,userId,pageRequest);
        Page<ParticipantElement> modifyPage = page.map(query -> ParticipantElement.from(query, (fileUrl) -> fileUrl + url));
        List<ParticipantElement> content = modifyPage.getContent();
        boolean hasNext = modifyPage.hasNext();
        return FetchParticipantResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }
}
