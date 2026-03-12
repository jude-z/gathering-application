package infra.redis.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class IdGenerator {
    private final RedisTemplate<String,String> redisTemplate;
    @Value("${chat-message-send.id}")
    private String sendKey;
    @Value("${chat-message-reply.id}")
    private String replyKey;

    public Long nextIdForSend(){
        return redisTemplate.opsForValue().increment(sendKey);
    }
    public Long nextIdForReply(){
        return redisTemplate.opsForValue().increment(replyKey);
    }
}
