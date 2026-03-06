package api.controller.recommend;

import api.response.ApiResponse;
import api.service.recommend.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse> recommend(){
        ApiResponse apiResponse = recommendService.fetchRecommendTop10(LocalDateTime.now().toLocalDate());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
