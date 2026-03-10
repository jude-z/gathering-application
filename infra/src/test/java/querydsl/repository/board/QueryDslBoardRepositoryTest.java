package querydsl.repository.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import infra.dto.querydsl.board.BoardProjection;
import infra.dto.querydsl.board.BoardsProjection;
import entity.board.Board;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.board.QueryDslBoardRepository;
import jakarta.persistence.EntityManager;
import infra.repository.board.BoardRepository;
import infra.repository.category.CategoryRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslBoardRepositoryTest {

    QueryDslBoardRepository queryDslBoardRepository;

    @Autowired EntityManager em;
    @Autowired BoardRepository boardRepository;
    @Autowired GatheringRepository gatheringRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;

    Image image;
    User user;
    Category category;
    Gathering gathering;

    @BeforeEach
    void beforeEach() {
        queryDslBoardRepository = new QueryDslBoardRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        user = returnDummyUser(1, image);
        category = returnDummyCategory(1);

        imageRepository.save(image);
        userRepository.save(user);
        categoryRepository.save(category);

        gathering = returnDummyGathering(1, category, user, image);
        gatheringRepository.save(gathering);
    }

    @Test
    void fetchBoard() {
        Board board = returnDummyBoard(user, gathering, 1);
        boardRepository.save(board);

        QueryDslPageResponse<BoardProjection> result = queryDslBoardRepository
                .fetchBoard(PageableInfo.of(0, 10), board.getId());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title1");
    }

    @Test
    void fetchBoards() {
        Board board1 = returnDummyBoard(user, gathering, 1);
        Board board2 = returnDummyBoard(user, gathering, 2);
        boardRepository.saveAll(List.of(board1, board2));

        QueryDslPageResponse<BoardsProjection> result = queryDslBoardRepository
                .fetchBoards(PageableInfo.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void fetchBoards_empty() {
        QueryDslPageResponse<BoardsProjection> result = queryDslBoardRepository
                .fetchBoards(PageableInfo.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }
}
