package api.service.like;

import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import entity.gathering.Gathering;
import entity.like.Like;
import entity.user.User;
import exception.CommonException;
import infra.repository.gathering.GatheringRepository;
import infra.repository.like.LikeRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import infra.repository.like.QueryDslLikeRepository;

import static exception.Status.*;


@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;
    private final QueryDslLikeRepository queryDslLikeRepository;

    public ApiResponse like(Long gatheringId, Long userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            Gathering gathering = gatheringRepository.findById(gatheringId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_GATHERING));
            if(queryDslLikeRepository.findLike(userId,gatheringId).isPresent())
                throw new CommonException(ALREADY_LIKE);
            likeRepository.save(Like.of(gathering,user));
            return ApiStatusResponse.of(SUCCESS);
    }
    public ApiResponse dislike(Long gatheringId, Long userId) {

            userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(NOT_FOUND_USER));
            gatheringRepository.findById(gatheringId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_GATHERING));
            Like like = queryDslLikeRepository.findLike(userId, gatheringId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_LIKE));
            likeRepository.delete(like);
            return ApiStatusResponse.of(SUCCESS);

    }

}
