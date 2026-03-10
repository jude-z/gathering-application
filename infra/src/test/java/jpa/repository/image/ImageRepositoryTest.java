package jpa.repository.image;

import config.TestConfig;
import entity.image.Image;
import infra.repository.image.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @Test
    void findByUrl() {
        Image image = returnDummyImage(1);
        imageRepository.save(image);

        Optional<Image> result = imageRepository.findByUrl("image1Url");

        assertThat(result).isPresent();
        assertThat(result.get().getUrl()).isEqualTo("image1Url");
    }

    @Test
    void findByUrl_notFound() {
        Optional<Image> result = imageRepository.findByUrl("nonExistent");

        assertThat(result).isEmpty();
    }
}
