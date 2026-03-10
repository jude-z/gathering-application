package api.common.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmitterRepositoryImpl implements EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String clientId, SseEmitter sseEmitter) {
        emitters.put(clientId, sseEmitter);
        return sseEmitter;
    }
    @Override
    public void deleteById(String clientId) {
        emitters.remove(clientId);
    }

    @Override
    public SseEmitter findById(String clientId) {
        return emitters.get(clientId);
    }
}
