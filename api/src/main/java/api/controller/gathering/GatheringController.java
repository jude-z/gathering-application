package api.controller.gathering;

import api.common.annotation.Username;
import api.response.ApiResponse;
import api.service.gathering.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static api.requeset.gathering.GatheringRequestDto.*;

@RestController
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping("/gathering")
    public ResponseEntity<ApiResponse> addGathering(@RequestPart AddGatheringRequest addGatheringRequest,
                                                    @RequestPart MultipartFile file,
                                                    @Username Long userId) throws IOException {
        ApiResponse apiResponse = gatheringService.addGathering(addGatheringRequest, file, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/gathering/{gatheringId}")
    public ResponseEntity<ApiResponse> updateGathering(@RequestPart UpdateGatheringRequest updateGatheringRequest,
                                                       @PathVariable Long gatheringId,
                                                       @RequestPart(required = false) MultipartFile file,
                                                       @Username Long userId) throws IOException {
        ApiResponse apiResponse = gatheringService.updateGathering(updateGatheringRequest, file, userId, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}")
    public ResponseEntity<ApiResponse> gatheringDetail(@PathVariable Long gatheringId){
        ApiResponse apiResponse = gatheringService.gatheringDetail(gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/participated/{gatheringId}")
    public ResponseEntity<ApiResponse> participated(@PathVariable Long gatheringId, Integer pageNum, Integer pageSize){
        ApiResponse apiResponse = gatheringService.participated(gatheringId, pageNum, pageSize);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gatherings")
    public ResponseEntity<ApiResponse> gatherings(){
        ApiResponse apiResponse = gatheringService.gatherings();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering")
    public ResponseEntity<ApiResponse> gatheringCategory(@RequestParam String category,
                                                         @RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize){
        ApiResponse apiResponse = gatheringService.gatheringCategory(category, pageNum, pageSize);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/gatherings/like")
    public ResponseEntity<ApiResponse> gatheringsLike(@RequestParam int pageNum,
                                                      @RequestParam Integer pageSize,
                                                      @Username Long userId){
        ApiResponse apiResponse = gatheringService.gatheringsLike(pageNum, pageSize, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
