package send.service;

import org.springframework.stereotype.Service;

@Service
public class ChatSendService {
    public boolean isRoomParticipant(Long userId, Long chatRoomId){
        return true;
    }
}
