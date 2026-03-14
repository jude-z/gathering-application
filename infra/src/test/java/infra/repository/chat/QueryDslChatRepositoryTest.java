package infra.repository.chat;

import entity.chat.ChatRoom;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.chat.*;
import infra.support.RepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import page.PageableInfo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QueryDslChatRepositoryTest extends RepositoryTestSupport {

    @Autowired
    QueryDslChatRepository queryDslChatRepository;

    @Test
    @DisplayName("fetchUnReadMessages - 읽지 않은 메시지 조회")
    void fetchUnReadMessages_읽지_않은_메시지_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<ChatMessageProjection> result =
                queryDslChatRepository.fetchUnReadMessages(pageableInfo, chatRoom1.getId(), user2.getId());

        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("fetchParticipant - 채팅방 참여자 조회")
    void fetchParticipant_참여자_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<ParticipantProjection> result =
                queryDslChatRepository.fetchParticipant(pageableInfo, chatRoom1.getId(), user1.getId());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("fetchMyChatRooms - 내 채팅방 목록 조회")
    void fetchMyChatRooms_내_채팅방_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<MyChatRoomProjection> result =
                queryDslChatRepository.fetchMyChatRooms(pageableInfo, user1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getChatRoomTitle()).isEqualTo("Chat Room 1");
    }

    @Test
    @DisplayName("fetchChatRooms - 모임의 전체 채팅방 조회")
    void fetchChatRooms_전체_채팅방_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<ChatRoomProjection> result =
                queryDslChatRepository.fetchChatRooms(pageableInfo, user1.getId(), gathering1.getId());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("fetchAbleChatRooms - 참여 가능한 채팅방 조회")
    void fetchAbleChatRooms_참여_가능_채팅방_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<AbleChatRoomProjection> result =
                queryDslChatRepository.fetchAbleChatRooms(pageableInfo, user1.getId(), gathering1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getChatRoomTitle()).isEqualTo("Chat Room 2");
    }

    @Test
    @DisplayName("fetchParticipateChatRooms - 참여중인 채팅방 조회")
    void fetchParticipateChatRooms_참여중_채팅방_조회() {
        PageableInfo pageableInfo = PageableInfo.of(0, 10);

        QueryDslPageResponse<ParticipateChatRoomProjection> result =
                queryDslChatRepository.fetchParticipateChatRooms(pageableInfo, user1.getId(), gathering1.getId());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getChatRoomTitle()).isEqualTo("Chat Room 1");
    }

    @Test
    @DisplayName("fetchChatRoomById - 채팅방 상세 조회")
    void fetchChatRoomById_상세_조회() {
        Optional<ChatRoom> result = queryDslChatRepository.fetchChatRoomById(chatRoom1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Chat Room 1");
        assertThat(result.get().getCreatedBy().getUsername()).isEqualTo("user1");
    }
}
