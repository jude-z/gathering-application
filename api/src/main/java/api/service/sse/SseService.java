package api.service.sse;

import api.common.sse.EmitterRepository;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import entity.user.Fail;
import exception.Status;
import infra.repository.fail.FailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static api.requeset.user.UserRequestDto.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SseService {
    private final FailRepository failRepository;
    private final EmitterRepository emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(String clientId, String lastEventId) {
        SseEmitter emitter = emitterRepository.save(clientId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(clientId));
        emitter.onTimeout(() -> emitterRepository.deleteById(clientId));
        emitter.onError((e)->{
            log.error(e.getMessage(),e);
            emitterRepository.deleteById(clientId);
        });
        sendToClient(emitter, clientId, "EventStream Created. [clientId=" + clientId + "]");
        return emitter;
    }

    public void send(EmailCertificationRequest emailCertificationRequest){
        String clientId = emailCertificationRequest.getClientId();
        String email = emailCertificationRequest.getEmail();
        SseEmitter sseEmitter = emitterRepository.findById(clientId);
        Fail fail = Fail.of(clientId,"fail send Mail",email);
        sendToClient(sseEmitter,clientId,fail);
        failRepository.save(fail);
    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("connect Fail!");
        }
    }

    public ApiResponse close(String clientId) {
        SseEmitter emitter = emitterRepository.findById(clientId);
        emitter.complete();
        emitterRepository.deleteById(clientId);
        return ApiStatusResponse.of(Status.SUCCESS);
    }
}
