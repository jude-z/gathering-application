package infra.repository.certification;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.certification.QCertification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static entity.certification.QCertification.*;

@Repository
@RequiredArgsConstructor
public class QueryDslCertificationRepository {
    private final JPAQueryFactory queryFactory;


    public String findCertificationByEmail(String email) {
        return queryFactory.select(certification1.certification)
                .from(certification1)
                .where(certification1.email.eq(email))
                .orderBy(certification1.id.desc())
                .fetchFirst();
    }
}
