package querydsl.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import config.TestConfig;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.category.Category;
import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.chat.ReadStatus;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.dto.querydsl.chat.*;
import infra.repository.chat.*;
import jakarta.persistence.EntityManager;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
class QueryDslChatRepositoryTest {

    QueryDslChatRepository queryDslChatRepository;

    @Autowired EntityManager em;
    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired ChatParticipantRepository chatParticipantRepository;
    @Autowired ChatMessageRepository chatMessageRepository;
    @Autowired ReadStatusRepository readStatusRepository;
    @Autowired UserRepository userRepository;
    @Autowired ImageRepository imageRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired GatheringRepository gatheringRepository;

    Image image;
    List<User> users;
    Category category;
    Gathering gathering;
    ChatRoom chatRoom;
    List<ChatParticipant> participants;

    @BeforeEach
    void beforeEach() {
        queryDslChatRepository = new QueryDslChatRepository(new JPAQueryFactory(em));
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image), returnDummyUser(3, image));
        category = returnDummyCategory(1);

        imageRepository.save(image);
        userRepository.saveAll(users);
        categoryRepository.save(category);

        gathering = returnDummyGathering(1, category, users.get(0), image);
        gatheringRepository.save(gathering);

        chatRoom = returnDummyChatRoom(users.get(0), gathering, 1);
        chatRoomRepository.save(chatRoom);

        participants = List.of(
                returnDummyChatParticipant(users.get(0), chatRoom),
                returnDummyChatParticipant(users.get(1), chatRoom));
        chatParticipantRepository.saveAll(participants);
    }

    @Test
    void fetchUnReadMessages() {
        ChatMessage msg = returnDummyChatMessage(chatRoom, participants.get(1), 1);
        chatMessageRepository.save(msg);

        ReadStatus rs = returnDummyReadStatus(participants.get(0), msg);
        readStatusRepository.save(rs);

        QueryDslPageResponse<ChatMessageProjection> result = queryDslChatRepository
                .fetchUnReadMessages(PageableInfo.of(0, 10), chatRoom.getId(), users.get(0).getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void fetchUnReadMessages_allRead() {
        QueryDslPageResponse<ChatMessageProjection> result = queryDslChatRepository
                .fetchUnReadMessages(PageableInfo.of(0, 10), chatRoom.getId(), users.get(0).getId());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void fetchParticipant() {
        QueryDslPageResponse<ParticipantProjection> result = queryDslChatRepository
                .fetchParticipant(PageableInfo.of(0, 10), chatRoom.getId(), users.get(0).getId());

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void fetchMyChatRooms() {
        QueryDslPageResponse<MyChatRoomProjection> result = queryDslChatRepository
                .fetchMyChatRooms(PageableInfo.of(0, 10), users.get(0).getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getChatRoomTitle()).isEqualTo("title1");
    }

    @Test
    void fetchChatRooms() {
        QueryDslPageResponse<ChatRoomProjection> result = queryDslChatRepository
                .fetchChatRooms(PageableInfo.of(0, 10), users.get(0).getId(), gathering.getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void fetchAbleChatRooms() {
        QueryDslPageResponse<AbleChatRoomProjection> result = queryDslChatRepository
                .fetchAbleChatRooms(PageableInfo.of(0, 10), users.get(2).getId(), gathering.getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void fetchAbleChatRooms_alreadyParticipant() {
        QueryDslPageResponse<AbleChatRoomProjection> result = queryDslChatRepository
                .fetchAbleChatRooms(PageableInfo.of(0, 10), users.get(0).getId(), gathering.getId());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void fetchParticipateChatRooms() {
        QueryDslPageResponse<ParticipateChatRoomProjection> result = queryDslChatRepository
                .fetchParticipateChatRooms(PageableInfo.of(0, 10), users.get(0).getId(), gathering.getId());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void fetchParticipateChatRooms_notParticipant() {
        QueryDslPageResponse<ParticipateChatRoomProjection> result = queryDslChatRepository
                .fetchParticipateChatRooms(PageableInfo.of(0, 10), users.get(2).getId(), gathering.getId());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void fetchChatRoomById() {
        Optional<ChatRoom> result = queryDslChatRepository.fetchChatRoomById(chatRoom.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("title1");
    }

    @Test
    void fetchChatRoomById_notFound() {
        Optional<ChatRoom> result = queryDslChatRepository.fetchChatRoomById(999L);

        assertThat(result).isEmpty();
    }
}
