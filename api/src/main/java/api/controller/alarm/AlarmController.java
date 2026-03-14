package api.controller.alarm;

import api.common.resolver.annotation.Username;
import api.response.ApiResponse;
import api.service.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PatchMapping("/alarm/{id}")
    public ResponseEntity<ApiResponse> checkAlarm(Long id, @Username Long userId){
        ApiResponse apiResponse = alarmService.checkAlarm(id, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/alarm/{id}")
    public ResponseEntity<ApiResponse> deleteAlarm(Long id, @Username Long userId){
        ApiResponse apiResponse = alarmService.deleteAlarm(id, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/alarm")
    public ResponseEntity<ApiResponse> alarmList(@RequestParam Integer pageNum,
                                                 @RequestParam Integer pageSize,
                                                 @Username Long userId,
                                                 @RequestParam Boolean checked){
        ApiResponse apiResponse = alarmService.alarmList(pageNum, pageSize,userId, checked);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
