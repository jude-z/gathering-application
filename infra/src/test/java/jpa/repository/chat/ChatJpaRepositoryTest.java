package jpa.repository.chat;

import config.TestConfig;
import entity.category.Category;
import entity.chat.ChatMessage;
import entity.chat.ChatParticipant;
import entity.chat.ChatRoom;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import infra.repository.category.CategoryRepository;
import infra.repository.chat.ChatMessageRepository;
import infra.repository.chat.ChatParticipantRepository;
import infra.repository.chat.ChatRoomRepository;
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
class ChatJpaRepositoryTest {

    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ChatParticipantRepository chatParticipantRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
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
    List<ChatMessage> messages;

    @BeforeEach
    void beforeEach() {
        image = returnDummyImage(1);
        users = List.of(returnDummyUser(1, image), returnDummyUser(2, image), returnDummyUser(3, image));
        category = returnDummyCategory(1);
        gathering = returnDummyGathering(1, category, users.get(0), image);
        chatRoom = returnDummyChatRoom(users.get(0), gathering, 1);
        participants = List.of(
                returnDummyChatParticipant(users.get(0), chatRoom),
                returnDummyChatParticipant(users.get(1), chatRoom),
                returnDummyChatParticipant(users.get(2), chatRoom));
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
    void findChatMessageByChatRoom() {
        saveBaseData();
        messages = List.of(
                returnDummyChatMessage(chatRoom, participants.get(0), 1),
                returnDummyChatMessage(chatRoom, participants.get(0), 2),
                returnDummyChatMessage(chatRoom, participants.get(1), 3));
        chatMessageRepository.saveAll(messages);

        List<ChatMessage> result = chatMessageRepository.findChatMessageByChatRoom(chatRoom);

        assertThat(result).hasSize(3);
        assertThat(result).extracting("content")
                .containsExactly("content1", "content2", "content3");
    }

    @Test
    void findByChatRoomAndUserAndStatus() {
        saveBaseData();

        Optional<ChatParticipant> result = chatParticipantRepository
                .findByChatRoomAndUserAndStatus(chatRoom, users.get(0), true);

        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(users.get(0));
    }

    @Test
    void findAllByChatRoomAndStatus() {
        saveBaseData();

        List<ChatParticipant> result = chatParticipantRepository
                .findAllByChatRoomAndStatus(chatRoom, true);

        assertThat(result).hasSize(3);
    }
}
