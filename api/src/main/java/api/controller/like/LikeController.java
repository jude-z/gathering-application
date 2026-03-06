package api.controller.like;

import api.common.annotation.Username;
import api.response.ApiResponse;
import api.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PatchMapping("/gathering/{gatheringId}/like")
    public ResponseEntity<ApiResponse> like(@PathVariable Long gatheringId, @Username Long userId){
        ApiResponse apiResponse = likeService.like(gatheringId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/gathering/{gatheringId}/dislike")
    public ResponseEntity<ApiResponse> dislike(@PathVariable Long gatheringId, @Username Long userId){
        ApiResponse apiResponse = likeService.dislike(gatheringId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
