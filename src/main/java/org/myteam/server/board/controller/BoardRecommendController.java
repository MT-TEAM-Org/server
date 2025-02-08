package org.myteam.server.board.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardRecommendService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/board/recommend")
@RequiredArgsConstructor
public class BoardRecommendController {

    private final BoardRecommendService boardRecommendService;

    /**
     * 게시글 추천
     */
    @PostMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Void>> recommendBoard(@PathVariable Long boardId) {
        boardRecommendService.recommendBoard(boardId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시글 추천 성공", null));
    }

    /**
     * 게시글 추천 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable Long boardId) {
        boardRecommendService.deleteRecommendBoard(boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 추천 삭제 성공", null));
    }
}