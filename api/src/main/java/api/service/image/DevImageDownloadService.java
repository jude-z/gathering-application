package api.service.image;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class DevImageDownloadService implements ImageDownloadService{
    @Override
    public Resource getFileByteArrayFromS3(String fileName) throws IOException {
        return null;
    }
}
