package querydsl.repository.certification;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.repository.certification.QueryDslCertificationRepository;
import jakarta.persistence.EntityManager;
import infra.repository.certification.CertificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslCertificationRepositoryTest {

    QueryDslCertificationRepository queryDslCertificationRepository;

    @Autowired EntityManager em;
    @Autowired CertificationRepository certificationRepository;

    @BeforeEach
    void beforeEach() {
        queryDslCertificationRepository = new QueryDslCertificationRepository(new JPAQueryFactory(em));
    }

    @Test
    void findCertificationByEmail() {
        certificationRepository.save(returnDummyCertification("test@email.com", "code1"));
        certificationRepository.save(returnDummyCertification("test@email.com", "code2"));

        String result = queryDslCertificationRepository.findCertificationByEmail("test@email.com");

        assertThat(result).isEqualTo("code2");
    }

    @Test
    void findCertificationByEmail_notFound() {
        String result = queryDslCertificationRepository.findCertificationByEmail("nonExistent@email.com");

        assertThat(result).isNull();
    }
}
