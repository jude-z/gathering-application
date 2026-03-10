package api.controller.image;

import api.response.ApiResponse;
import api.service.image.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/image/{imageUrl}")
    public Resource fetchImage(@PathVariable String imageUrl, HttpServletResponse response) throws IOException {
        return imageService.fetchImage(imageUrl, response);
    }

    @GetMapping("/gathering/{gatheringId}/image")
    public ResponseEntity<ApiResponse> gatheringImage(@PathVariable Long gatheringId, @RequestParam Integer pageNum,@RequestParam Integer pageSize){
        ApiResponse apiResponse = imageService.gatheringImage(gatheringId, pageNum,pageSize);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
