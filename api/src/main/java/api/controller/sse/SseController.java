package api.controller.sse;

import api.response.ApiResponse;
import api.service.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    @GetMapping(value = "/subscribe/{clientId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String clientId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return sseService.subscribe(clientId, lastEventId);
    }

    @DeleteMapping(value = "/close/{clientId}")
    public ResponseEntity<ApiResponse> close(@PathVariable String clientId){
        ApiResponse apiResponse = sseService.close(clientId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
