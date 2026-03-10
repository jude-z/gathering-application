package jdbc.repository.chat;

import config.TestConfig;
import entity.category.Category;
import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.chat.ReadStatus;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.chat.*;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static utils.DummyData.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ContextConfiguration(classes = TestConfig.class)
@Import(JdbcChatRepository.class)
class JdbcChatRepositoryTest {

    @Autowired JdbcChatRepository jdbcChatRepository;
    @Autowired ChatMessageRepository chatMessageRepository;
    @Autowired ChatParticipantRepository chatParticipantRepository;
    @Autowired ChatRoomRepository chatRoomRepository;
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
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image));
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, users.get(0), image);
        chatRoom = returnDummyChatRoom(users.get(0), gathering, 1);
        participants = List.of(
                returnDummyChatParticipant(users.get(0), chatRoom),
                returnDummyChatParticipant(users.get(1), chatRoom));
    }

    private void saveBaseData() {
        imageRepository.save(image);
        categoryRepository.save(category);
        userRepository.saveAll(users);
        gatheringRepository.save(gathering);
        chatRoomRepository.save(chatRoom);
        chatParticipantRepository.saveAll(participants);
    }

    @Test
    void readChatMessage() {
        saveBaseData();
        ChatMessage msg1 = returnDummyChatMessage(chatRoom, participants.get(1), 1);
        ChatMessage msg2 = returnDummyChatMessage(chatRoom, participants.get(1), 2);
        chatMessageRepository.saveAll(List.of(msg1, msg2));

        ReadStatus rs1 = returnDummyReadStatus(participants.get(0), msg1);
        ReadStatus rs2 = returnDummyReadStatus(participants.get(0), msg2);
        readStatusRepository.saveAll(List.of(rs1, rs2));

        jdbcChatRepository.readChatMessage(participants.get(0).getId(), List.of(msg1.getId(), msg2.getId()));

        List<ReadStatus> result = readStatusRepository.findAll();
        assertThat(result).allMatch(ReadStatus::getStatus);
    }

    @Test
    void readChatMessage_emptyList() {
        saveBaseData();

        jdbcChatRepository.readChatMessage(participants.get(0).getId(), List.of());

        assertThat(readStatusRepository.findAll()).isEmpty();
    }
}
