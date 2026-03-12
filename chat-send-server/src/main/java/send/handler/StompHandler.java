package send.handler;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import send.service.ChatSendService;

import javax.crypto.SecretKey;

@Component
public class StompHandler implements ChannelInterceptor {

    private final SecretKey secretKey;
    private final ChatSendService chatSendService;

    public StompHandler(@Value("${jwt.secretKey}") String secretKey
            ,ChatSendService chatSendService) {
        this.secretKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));
        this.chatSendService = chatSendService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);


        if (StompCommand.CONNECT == accessor.getCommand()) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
//        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
//            if(!chatSendService.isRoomParticipant(userId, Long.parseLong(roomId))){
//                throw new NotAuthorizeException("No Authority");
//            }
//        }
        return message;
    }
}
