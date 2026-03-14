package api.service.like;

import api.response.ApiResponse;
import entity.gathering.Gathering;
import entity.like.Like;
import entity.user.User;
import exception.CommonException;
import infra.repository.gathering.GatheringRepository;
import infra.repository.like.LikeRepository;
import infra.repository.like.QueryDslLikeRepository;
import infra.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private QueryDslLikeRepository queryDslLikeRepository;

    @Test
    @DisplayName("like 성공")
    void like_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        given(queryDslLikeRepository.findLike(1L, 1L)).willReturn(Optional.empty());

        ApiResponse response = likeService.like(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("like 사용자 없으면 CommonException")
    void like_사용자없음() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.like(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("like 모임 없으면 CommonException")
    void like_모임없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(gatheringRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.like(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("like 이미 좋아요 시 CommonException")
    void like_이미좋아요() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        Like like = Like.builder().gathering(gathering).likedBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        given(queryDslLikeRepository.findLike(1L, 1L)).willReturn(Optional.of(like));

        assertThatThrownBy(() -> likeService.like(1L, 1L))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("dislike 성공")
    void dislike_성공() {
        User user = User.builder().username("u").password("p").nickname("n").build();
        Gathering gathering = Gathering.builder().title("t").content("c").build();
        Like like = Like.builder().gathering(gathering).likedBy(user).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(gathering));
        given(queryDslLikeRepository.findLike(1L, 1L)).willReturn(Optional.of(like));

        ApiResponse response = likeService.dislike(1L, 1L);

        assertThat(response.getCode()).isEqualTo("SU");
        verify(likeRepository).delete(like);
    }

    @Test
    @DisplayName("dislike 좋아요 없으면 CommonException")
    void dislike_좋아요없음() {
        given(userRepository.findById(1L)).willReturn(Optional.of(User.builder().username("u").password("p").nickname("n").build()));
        given(gatheringRepository.findById(1L)).willReturn(Optional.of(Gathering.builder().title("t").content("c").build()));
        given(queryDslLikeRepository.findLike(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.dislike(1L, 1L))
                .isInstanceOf(CommonException.class);
    }
}
