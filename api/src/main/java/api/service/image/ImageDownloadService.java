package api.service.image;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface ImageDownloadService {
    Resource getFileByteArrayFromS3(String fileName) throws IOException;
}
