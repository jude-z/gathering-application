package api.controller.chat;

import api.common.resolver.annotation.Username;
import api.response.ApiResponse;
import api.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static api.requeset.chat.ChatRequestDto.*;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/my/chats")
    public ResponseEntity<ApiResponse> fetchMyChatRooms(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @Username Long userId){
        ApiResponse apiResponse = chatService.fetchMyChatRooms(pageNum, pageSize, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}/chats")
    public ResponseEntity<ApiResponse> fetchChatRooms(@PathVariable Long gatheringId, @RequestParam Integer pageNum, @RequestParam Integer pageSize, @Username Long userId){
        ApiResponse apiResponse = chatService.fetchChatRooms(gatheringId, pageNum, pageSize, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}/able/chats")
    public ResponseEntity<ApiResponse> fetchAbleChatRooms(@PathVariable Long gatheringId, @RequestParam Integer pageNum, @RequestParam Integer pageSize, @Username Long userId){
        ApiResponse apiResponse = chatService.fetchAbleChatRooms(gatheringId, pageNum, pageSize, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}/participate/chats")
    public ResponseEntity<ApiResponse> fetchParticipateChatRooms(@PathVariable Long gatheringId, @RequestParam Integer pageNum, @RequestParam Integer pageSize, @Username Long userId){
        ApiResponse apiResponse = chatService.fetchParticipateChatRooms(gatheringId, pageNum, pageSize, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/gathering/{gatheringId}/chat")
    public ResponseEntity<ApiResponse> addChatRoom(@PathVariable Long gatheringId, @RequestBody AddChatRequest addChatRequest, @Username Long userId){
        ApiResponse apiResponse = chatService.addChatRoom(gatheringId, addChatRequest, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/chat/attend/{chatId}")
    public ResponseEntity<ApiResponse> attendChat(@PathVariable Long chatId, @Username Long userId){
        ApiResponse apiResponse = chatService.attendChat(chatId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/chat/disAttend/{chatId}")
    public ResponseEntity<ApiResponse> leaveChat(@PathVariable Long chatId, @Username Long userId){
        ApiResponse apiResponse = chatService.leaveChat(chatId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/messages/{chatId}")
    public ResponseEntity<ApiResponse> fetchMessages(@PathVariable Long chatId, @Username Long userId){
        ApiResponse apiResponse = chatService.fetchUnReadMessages(chatId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/chat/{chatId}")
    public ResponseEntity<ApiResponse> readChatMessage(@PathVariable Long chatId, @Username Long userId){
        ApiResponse apiResponse = chatService.readChatMessage(chatId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<ApiResponse> fetchChat(@PathVariable Long chatId, @Username Long userId){
        ApiResponse apiResponse = chatService.fetchChat(chatId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/chat/participant/{chatId}")
    public ResponseEntity<ApiResponse> fetchParticipant(@PathVariable Long chatId, @Username Long userId,
                                                        @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        ApiResponse apiResponse = chatService.fetchParticipant(chatId, userId, pageNum, pageSize);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
