package api.service.board;

import api.response.ApiDataResponse;
import api.response.ApiResponse;
import api.service.image.ImageUploadService;
import infra.dto.PageableInfo;
import infra.dto.querydsl.QueryDslPageResponse;
import infra.dto.querydsl.board.BoardProjection;
import infra.dto.querydsl.board.BoardsProjection;
import entity.board.Board;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import exception.CommonException;
import jakarta.transaction.Transactional;
import infra.repository.board.BoardRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import static api.response.board.BoardResponseDto.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import infra.repository.board.QueryDslBoardRepository;
import util.PageCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static api.requeset.board.BoardRequestDto.*;
import static exception.Status.*;


@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;
    private final GatheringRepository gatheringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QueryDslBoardRepository queryDslBoardRepository;
    @Value("${server.url}")
    private String url;

    public ApiResponse fetchBoard(Long gatheringId, Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_USER));
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
        if(enrollmentRepository.findByGatheringAndEnrolledBy(gathering,user).isEmpty()) throw new CommonException(NOT_AUTHORIZE);
        PageableInfo boardPageableInfo = PageCalculator.toPageableInfo(1, Integer.MAX_VALUE);
        QueryDslPageResponse<BoardProjection> boardPage = queryDslBoardRepository.fetchBoard(boardPageableInfo, boardId);
        List<BoardProjection> boardProjections = boardPage.getContent();
        if(boardProjections.isEmpty()) throw new CommonException(NOT_FOUND_BOARD);
        String userImageUrl = getUserImageUrl(boardProjections);
        List<String> imageUrls = getImageUrls(boardProjections);
        BoardResponse boardResponse = BoardResponse.from(boardProjections, imageUrls, userImageUrl);
        return ApiDataResponse.of(boardResponse, SUCCESS);
    }

    public ApiResponse addBoard(Long userId, AddBoardRequest addBoardRequest, List<MultipartFile> files, Long gatheringId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()->new CommonException(NOT_FOUND_USER));
        Gathering gathering = gatheringRepository.findById(gatheringId).orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findByGatheringAndEnrolledBy(gathering, user);
        if(optionalEnrollment.isEmpty()) throw new CommonException(NOT_AUTHORIZE);
        Board board = AddBoardRequest.of(addBoardRequest,user,gathering);
        saveImages(files,board,gathering);
        boardRepository.save(board);
        return ApiDataResponse.of(board.getId(), SUCCESS);
    }

    public ApiResponse fetchBoards(Long gatheringId, Integer pageNum, Integer pageSize) {
        gatheringRepository.findById(gatheringId).orElseThrow(() -> new CommonException(NOT_FOUND_GATHERING));
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<BoardsProjection> queryDslPageResponse = queryDslBoardRepository.fetchBoards(pageableInfo);
        List<BoardsProjection> content = queryDslPageResponse.getContent();
        List<BoardElement> boardElements = content.stream()
                .map(projection -> BoardElement.from(projection, url))
                .toList();
        return ApiDataResponse.of(boardElements,SUCCESS);
    }

    private String getUserImageUrl(List<BoardProjection> boardProjections){
        return getUrl(boardProjections.getFirst().getUserImageUrl());
    }

    private List<String> getImageUrls(List<BoardProjection> boardProjections) {
        List<String> imageUrls = new ArrayList<>();
        for (BoardProjection boardProjection : boardProjections) {
                imageUrls.add(getUrl(boardProjection.getImageUrl()));
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
                String url = imageUploadService.upload(file);
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
