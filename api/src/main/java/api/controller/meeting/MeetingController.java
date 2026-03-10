package api.controller.meeting;

import api.common.resolver.annotation.Username;
import api.response.ApiResponse;
import api.service.meeting.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static api.requeset.meeting.MeetingRequestDto.*;

@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/gathering/{gatheringId}/meeting")
    public ResponseEntity<ApiResponse> addMeeting(@RequestPart AddMeetingRequest addMeetingRequest,
                                                  @RequestPart MultipartFile file,
                                                  @PathVariable Long gatheringId,
                                                  @Username Long userId) throws IOException {
        ApiResponse apiResponse = meetingService.addMeeting(addMeetingRequest, userId, gatheringId, file);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/gathering/{gatheringId}/meeting/{meetingId}")
    public ResponseEntity<ApiResponse> deleteMeeting(@Username Long userId,
                                                     @PathVariable Long meetingId,
                                                     @PathVariable Long gatheringId) {
        ApiResponse apiResponse = meetingService.deleteMeeting(userId, meetingId, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/gathering/{gatheringId}/meeting/{meetingId}")
    public ResponseEntity<ApiResponse> updateMeeting(@RequestPart UpdateMeetingRequest updateMeetingRequest,
                                                     @RequestPart(required = false) MultipartFile file,
                                                     @Username Long userId,
                                                     @PathVariable Long meetingId,
                                                     @PathVariable Long gatheringId) throws IOException {
        ApiResponse apiResponse = meetingService.updateMeeting(updateMeetingRequest, userId, meetingId, file, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}/meeting/{meetingId}")
    public ResponseEntity<ApiResponse> meetingDetail(@PathVariable Long meetingId,
                                                     @PathVariable Long gatheringId){
        ApiResponse apiResponse = meetingService.meetingDetail(meetingId, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}/meetings")
    public ResponseEntity<ApiResponse> meetings(@RequestParam int pageNum,@RequestParam int pageSize,
                                                @PathVariable Long gatheringId){
        ApiResponse apiResponse = meetingService.meetings(pageNum, pageSize,gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
