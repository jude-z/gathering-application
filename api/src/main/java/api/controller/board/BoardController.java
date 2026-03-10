package api.controller.board;

import api.common.resolver.annotation.Username;
import api.response.ApiResponse;
import api.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static api.requeset.board.BoardRequestDto.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/gathering/{gatheringId}/board/{boardId}")
    public ResponseEntity<ApiResponse> fetchBoard(@PathVariable Long boardId,
                                                  @PathVariable Long gatheringId,
                                                  @Username Long userId){
        ApiResponse apiResponse = boardService.fetchBoard(gatheringId, boardId, userId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/gathering/{gatheringId}/board")
    public ResponseEntity<ApiResponse> addBoard(@Username Long userId,
                                                @PathVariable Long gatheringId,
                                                @RequestPart AddBoardRequest addBoardRequest,
                                                @RequestPart("files") List<MultipartFile> files) throws IOException {
        ApiResponse apiResponse = boardService.addBoard(userId, addBoardRequest, files, gatheringId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/gathering/{gatheringId}/boards")
    public ResponseEntity<ApiResponse> fetchBoards(@PathVariable Long gatheringId,
                                                   Integer pageNum, Integer pageSize){
        ApiResponse apiResponse = boardService.fetchBoards(gatheringId, pageNum, pageSize);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
