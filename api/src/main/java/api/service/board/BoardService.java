package api.service.board;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final S3ImageUploadService s3ImageUploadService;
    private final GatheringRepository gatheringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AsyncService asyncService;
    private final RecommendService recommendService;
    @Value("${server.url}")
    private String url;

    public BoardResponse fetchBoard(Long gatheringId, Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("no exist User!!"));
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
        if(enrollmentRepository.findByGatheringAndEnrolledBy(gathering,user).isEmpty()) throw new NotAuthorizeException("no Authorize to fetch board");
        List<BoardQuery> boardQueries = boardRepository.fetchBoard(boardId);
        if(boardQueries.isEmpty()) throw new NotFoundBoardException("no board found");
        String userImageUrl = getUserImageUrl(boardQueries);
        List<String> imageUrls = getImageUrls(boardQueries);
        recommendService.addScore(gatheringId,1);
        return BoardResponse.of(boardQueries,imageUrls,userImageUrl,SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public AddBoardResponse addBoard(Long userId, AddBoardRequest addBoardRequest, List<MultipartFile> files, Long gatheringId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundUserException("no exist User!!"));
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findByGatheringAndEnrolledBy(gathering, user);
        if(optionalEnrollment.isEmpty()) throw new NotAuthorizeException("no Authorize to add board");
        Board board = AddBoardRequest.of(addBoardRequest,user,gathering);
        saveImages(files,board,gathering);
        boardRepository.save(board);
        recommendService.addScore(gatheringId,1);

        return AddBoardResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE,board.getId());
    }

    public BoardsResponse fetchBoards(Long gatheringId, Integer pageNum, Integer pageSize) {
        gatheringRepository.findById(gatheringId).orElseThrow(() -> new NotFoundGatheringException("no exist Gathering!!"));
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<BoardsQuery> page = boardRepository.fetchBoards(pageRequest);
        List<BoardElement> content = toContent(page);
        boolean hasNext = page.hasNext();
        return BoardsResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    private List<BoardElement> toContent(Page<BoardsQuery> page) {
        return page.map(query -> BoardElement.from(query,(fileUrl)->url+fileUrl)).getContent();
    }

    private String getUserImageUrl(List<BoardQuery> boardQueries){

        return getUrl(boardQueries.getFirst().getUserImageUrl());

    }

    private List<String> getImageUrls(List<BoardQuery> boardQueries) {
        List<String> imageUrls = new ArrayList<>();
        for (BoardQuery boardQuery : boardQueries) {
                imageUrls.add(getUrl(boardQuery.getImageUrl()));
        }
        return imageUrls;
    }

    private String getUrl(String fileUrl){
        return url+fileUrl;
    }

    private void saveImages(List<MultipartFile> files,Board board,Gathering gathering) throws IOException {
        List<Image> images = board.getImages();
        for(MultipartFile file : files){
            if(!file.isEmpty()){
                String url = s3ImageUploadService.upload(file);
                String contentType = file.getContentType();
                if(StringUtils.hasText(url)){
                    Image image = Image.builder()
                        .url(url)
                        .board(board)
                        .gathering(gathering)
                        .contentType(contentType)
                        .build();
                    images.add(image);
                }
            }
        }
    }

}
