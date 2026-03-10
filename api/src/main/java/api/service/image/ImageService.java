package api.service.image;

import api.response.ApiDataResponse;
import api.response.ApiResponse;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.image.Image;
import exception.CommonException;
import exception.Status;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import infra.repository.image.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import infra.repository.image.QueryDslImageRepository;
import util.PageCalculator;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageDownloadService imageDownloadService;
    private final ImageRepository imageRepository;
    private final QueryDslImageRepository queryDslImageRepository;

    @Value("${server.url}")
    private String url;
    public Resource fetchImage(String imageUrl, HttpServletResponse response) throws IOException {
        Image image = imageRepository.findByUrl(imageUrl).orElseThrow(()-> new CommonException(Status.NOT_FOUND_IMAGE));
        String contentType = image.getContentType();
        response.setContentType(contentType);
        return imageDownloadService.getFileByteArrayFromS3(imageUrl);
    }

    public ApiResponse gatheringImage(Long gatheringId, Integer pageNum,Integer pageSize) {
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<String> queryDslPageResponse = queryDslImageRepository.gatheringImage(gatheringId,pageableInfo);
        List<String> content = queryDslPageResponse.getContent();
        List<String> urls = toList(content);
        return ApiDataResponse.of(urls, Status.SUCCESS);
    }

    private List<String> toList(List<String> urls){
        return urls.stream().map(this::getUrl)
                .toList();
    }
    private String getUrl(String fileUrl){
        return url+fileUrl;
    }
}
