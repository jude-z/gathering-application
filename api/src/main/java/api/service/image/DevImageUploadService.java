package api.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class DevImageUploadService implements ImageUploadService{
    @Override
    public String upload(MultipartFile file) throws IOException {
        return "";
    }
}
