package api.service.board;

import api.response.ApiResponse;
import api.service.image.ImageUploadService;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.user.User;
import exception.CommonException;
import infra.repository.board.BoardRepository;
import infra.repository.board.QueryDslBoardRepository;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.board.BoardProjection;
import infra.repository.dto.querydsl.board.BoardsProjection;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;
    @Mock private BoardRepository boardRepository;
    @Mock private UserRepository userRepository;
    @Mock private ImageUploadService imageUploadService;
    @Mock private GatheringRepository gatheringRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private QueryDslBoardRepository queryDslBoardRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(boardService, "url", "http://localhost:8080");
    }

    @Test
    @DisplayName("fetchBoard 성공")
    void fetchBoard_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        Enrollment enrollment = Enrollment.builder().gathering(gathering).enrolledBy(user).build();
        BoardProjection projection = BoardProjection.builder()
                .title("title").description("desc").registerDate(LocalDateTime.now())
                .username("u").userImageUrl("/img.png").imageUrl("/img2.png").build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        given(enrollmentRepository.findByGatheringAndEnrolledBy(gathering, user)).willReturn(Optional.of(enrollment));
        QueryDslPageResponse<BoardProjection> pageResponse = QueryDslPageResponse.<BoardProjection>builder()
                .content(List.of(projection)).totalCount(1).pageNum(1).pageSize(Integer.MAX_VALUE).totalPage(1).isEmpty(false).build();
        given(queryDslBoardRepository.fetchBoard(any(), eq(1L))).willReturn(pageResponse);

        ApiResponse response = boardService.fetchBoard(1L, 1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
    }

    @Test
    @DisplayName("fetchBoard 사용자 없으면 CommonException")
    void fetchBoard_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.fetchBoard(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchBoard 모임 없으면 CommonException")
    void fetchBoard_모임없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.fetchBoard(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchBoard 권한 없으면 CommonException")
    void fetchBoard_권한없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        given(enrollmentRepository.findByGatheringAndEnrolledBy(gathering, user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.fetchBoard(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchBoard 게시글 없으면 CommonException")
    void fetchBoard_게시글없음() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        Enrollment enrollment = Enrollment.builder().gathering(gathering).enrolledBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        given(enrollmentRepository.findByGatheringAndEnrolledBy(gathering, user)).willReturn(Optional.of(enrollment));
        QueryDslPageResponse<BoardProjection> pageResponse = QueryDslPageResponse.<BoardProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(Integer.MAX_VALUE).totalPage(0).isEmpty(true).build();
        given(queryDslBoardRepository.fetchBoard(any(), eq(1L))).willReturn(pageResponse);

        assertThatThrownBy(() -> boardService.fetchBoard(1L, 1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("addBoard 사용자 없으면 CommonException")
    void addBoard_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.addBoard(1L, null, null, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("fetchBoards 성공")
    void fetchBoards_성공() {
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        QueryDslPageResponse<BoardsProjection> pageResponse = QueryDslPageResponse.<BoardsProjection>builder()
                .content(List.of()).totalCount(0).pageNum(1).pageSize(10).totalPage(0).isEmpty(true).build();
        given(queryDslBoardRepository.fetchBoards(any())).willReturn(pageResponse);

        ApiResponse response = boardService.fetchBoards(1L, 1, 10);

        assertThat(response.getCode()).isEqualTo("SU");
    }
}
