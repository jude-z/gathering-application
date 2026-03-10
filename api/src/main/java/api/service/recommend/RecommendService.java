package api.service.recommend;

import jakarta.transaction.Transactional;
import infra.repository.recommend.JdbcRecommendRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.recommend.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class RecommendService {

    private final GatheringRepository gatheringRepository;
    private final RecommendRepository recommendRepository;
    private final JdbcRecommendRepository jdbcRecommendRepository;
    @Value("${path}")
    private String path;
    public void addScore(Long gatheringId,int val){
        jdbcRecommendRepository.updateCount(gatheringId, LocalDate.now(), val);
    }
    //TODO : Cache
//    @Cacheable(value = "recommend",key="#localDate")
//    public RecommendResponse fetchRecommendTop10(LocalDate localDate) {
//        List<GatheringsQuery> gatheringsQueries = gatheringRepository.gatheringsRecommend(localDate);
//        List<GatheringsResponse> content = gatheringsQueries.stream().map(query -> GatheringsResponse.from(query, (fileUrl) -> (url + fileUrl)))
//                .toList();
//        return RecommendResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content);
//    }

}
