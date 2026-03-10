package api.common.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    void deleteById(String emitterId);
    SseEmitter findById(String clientId);
}
