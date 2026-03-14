package api.service.recommend;

import infra.repository.gathering.GatheringRepository;
import infra.repository.recommend.JdbcRecommendRepository;
import infra.repository.recommend.RecommendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendServiceTest {

    @InjectMocks
    private RecommendService recommendService;
    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private JdbcRecommendRepository jdbcRecommendRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendService, "path", "/images");
    }

    @Test
    @DisplayName("addScore 호출 시 jdbcRecommendRepository.updateCount 호출")
    void addScore_성공() {
        recommendService.addScore(1L, 5);

        verify(jdbcRecommendRepository).updateCount(any(), any(), anyInt());
    }
}
