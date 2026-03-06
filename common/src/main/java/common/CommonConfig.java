package common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CommonConfig {

    @Bean
    public ImageUrlConverter imageUrlConverter(@Value("${server.url}") String serverUrl) {
        return new ImageUrlConverter(serverUrl);
    }
}
