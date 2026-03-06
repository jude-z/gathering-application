package api.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploadService {
    String upload(MultipartFile file) throws IOException;
}
