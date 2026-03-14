package api.service.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Service
public class DevImageUploadService implements ImageUploadService{
    @Override
    public String upload(MultipartFile file) throws IOException {
        return "";
    }
}
