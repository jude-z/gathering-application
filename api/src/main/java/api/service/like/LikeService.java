package api.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;
    private final RecommendService recommendService;

    public LikeResponse like(Long gatheringId, Long userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Gathering gathering = gatheringRepository.findById(gatheringId)
                    .orElseThrow(()-> new NotFoundGatheringException("no exist Gathering!!"));
            if(likeRepository.findLike(userId,gatheringId).isPresent())
                throw new AlreadyLikeGatheringException("Already Like Gathering!!");
            likeRepository.save(Like.of(gathering,user));
            recommendService.addScore(gatheringId,1);
            return LikeResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }
    public DislikeResponse dislike(Long gatheringId, Long userId) {

            userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            gatheringRepository.findById(gatheringId)
                    .orElseThrow(()-> new NotFoundGatheringException("no exist Gathering!!"));
            Like like = likeRepository.findLike(userId, gatheringId)
                    .orElseThrow(()-> new NotFoundLikeException("no exist Like"));
            likeRepository.delete(like);
            recommendService.addScore(gatheringId,-1);
            return DislikeResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

}
