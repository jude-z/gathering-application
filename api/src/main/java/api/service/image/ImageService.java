package api.service.image;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import spring.myproject.common.exception.image.NotFoundImageException;
import spring.myproject.common.s3.S3ImageDownloadService;
import spring.myproject.dto.response.image.GatheringImageResponse;
import spring.myproject.entity.image.Image;
import spring.myproject.repository.image.ImageRepository;

import java.io.IOException;
import java.util.List;

import static spring.myproject.utils.ConstClass.SUCCESS_CODE;
import static spring.myproject.utils.ConstClass.SUCCESS_MESSAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final S3ImageDownloadService s3ImageDownloadService;
    private final ImageRepository imageRepository;

    @Value("${server.url}")
    private String url;
    public Resource fetchImage(String imageUrl, HttpServletResponse response) throws IOException {
        Image image = imageRepository.findByUrl(imageUrl).orElseThrow(()-> new NotFoundImageException("not found image"));
        String contentType = image.getContentType();
        response.setContentType(contentType);
        return s3ImageDownloadService.getFileByteArrayFromS3(imageUrl);
    }

    public GatheringImageResponse gatheringImage(Long gatheringId,Integer pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum-1, 9);
        Page<String> page = imageRepository.gatheringImage(gatheringId, pageRequest);
        List<String> content = page.getContent();
        boolean hasNext = page.hasNext();
        List<String> urls = toList(content);
        return GatheringImageResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE, urls,hasNext);
    }

    private List<String> toList(List<String> urls){
        return urls.stream().map(u -> getUrl(u))
                .toList();
    }
    private String getUrl(String fileUrl){
        return url+fileUrl;
    }
}
