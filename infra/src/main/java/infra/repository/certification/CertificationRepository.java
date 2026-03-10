package infra.repository.certification;

import entity.certification.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
}
