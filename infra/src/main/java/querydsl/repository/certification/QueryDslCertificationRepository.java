package querydsl.repository.certification;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entity.certification.QCertification;
import lombok.RequiredArgsConstructor;

import static entity.certification.QCertification.*;

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
