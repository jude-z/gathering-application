package api.controller.attend;

import api.common.annotation.Username;
import api.response.ApiResponse;
import api.service.attend.AttendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AttendController {

    private final AttendService attendService;

    @PostMapping("/gathering/{gatheringId}/meeting/{meetingId}/attend")
    public ResponseEntity<ApiResponse> addAttend(@PathVariable Long meetingId,
                                                 @Username Long userId,
                                                 @PathVariable Long gatheringId){
        ApiResponse apiResponse = attendService.addAttend(meetingId, userId, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/gathering/{gatheringId}/meeting/{meetingId}/disAttend")
    public ResponseEntity<ApiResponse> disAttend(@PathVariable Long meetingId,
                                                 @Username Long userId,
                                                 @PathVariable Long gatheringId){
        ApiResponse apiResponse = attendService.disAttend(meetingId, userId, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
