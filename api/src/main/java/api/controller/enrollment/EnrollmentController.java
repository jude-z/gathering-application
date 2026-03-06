package api.controller.enrollment;

import api.common.annotation.Username;
import api.response.ApiResponse;
import api.service.enrollment.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PatchMapping("/gathering/{gatheringId}/participate")
    public ResponseEntity<ApiResponse> enrollGathering(@PathVariable Long gatheringId,
                                                       @Username Long userId){
        ApiResponse apiResponse = enrollmentService.enrollGathering(gatheringId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/gathering/{gatheringId}/disParticipate")
    public ResponseEntity<ApiResponse> disEnrollGathering(@PathVariable Long gatheringId,
                                                          @Username Long userId){
        ApiResponse apiResponse = enrollmentService.disEnrollGathering(gatheringId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/gathering/{gatheringId}/permit/{enrollmentId}")
    public ResponseEntity<ApiResponse> permit(@PathVariable Long gatheringId,
                                              @PathVariable Long enrollmentId,
                                              @Username Long userId){
        ApiResponse apiResponse = enrollmentService.permit(gatheringId, enrollmentId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
