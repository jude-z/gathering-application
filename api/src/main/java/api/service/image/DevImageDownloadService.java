package api.service.image;

import org.springframework.core.io.Resource;

import java.io.IOException;

public class DevImageDownloadService implements ImageDownloadService{
    @Override
    public Resource getFileByteArrayFromS3(String fileName) throws IOException {
        return null;
    }
}
