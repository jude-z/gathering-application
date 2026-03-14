package util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlConverter implements ImageUrlProcess {

    private final String path;

    public ImageUrlConverter(@Value("${path}") String path) {
        this.path = path;
    }

    @Override
    public String convert(String rawUrl) {
        return path + rawUrl;
    }
}
