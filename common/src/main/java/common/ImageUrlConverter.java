package common;

import org.springframework.beans.factory.annotation.Value;
import util.ImageUrlProcess;

public class ImageUrlConverter implements ImageUrlProcess {

    private final String serverUrl;

    public ImageUrlConverter(@Value("${server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public String convert(String rawUrl) {
        return serverUrl + rawUrl;
    }
}
