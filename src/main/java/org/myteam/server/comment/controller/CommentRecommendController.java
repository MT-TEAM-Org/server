package org.myteam.server.comment.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.service.CommentRecommendService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments/recommend")
@RequiredArgsConstructor
public class CommentRecommendController {

    private final CommentRecommendService commentRecommendService;

    /**
     * 댓글 추천
     */
    @PostMapping("/{commentId}")
    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long commentId) {
        commentRecommendService.recommendComment(commentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글 추천 성공",
                null
        ));
    }

    /**
     * 댓글 추천 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto<Void>> cancelRecommendComment(@PathVariable Long commentId) {
        commentRecommendService.cancelRecommendComment(commentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글 추천 삭제 성공",
                null
        ));
    }
}
