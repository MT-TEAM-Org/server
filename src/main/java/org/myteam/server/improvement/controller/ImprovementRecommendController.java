package org.myteam.server.improvement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.improvement.service.ImprovementCommentRecommendReadService;
import org.myteam.server.improvement.service.ImprovementCommentRecommendService;
import org.myteam.server.improvement.service.ImprovementCountService;
import org.myteam.server.improvement.service.ImprovementReplyRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/recommend/improvement")
@RequiredArgsConstructor
public class ImprovementRecommendController {

    private final ImprovementCountService improvementCountService;
    private final ImprovementCommentRecommendService improvementCommentRecommendService;
    private final ImprovementReplyRecommendService improvementReplyRecommendService;

    /**
     * 개선요청 추천
     */
    @PostMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<Void>> recommendImprovement(@PathVariable Long improvementId) {
        improvementCountService.recommendImprovement(improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 추천 성공",
                null));
    }

    /**
     * 개선요청 추천 삭제
     */
    @DeleteMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<Void>> deleteImprovement(@PathVariable Long improvementId) {
        improvementCountService.deleteRecommendImprovement(improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 추천 삭제 성공",
                null
        ));
    }

    /**
     * 개선요청 댓글 추천
     */
    @PostMapping("/comment/{improvementCommentId}")
    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long improvementCommentId) {
        improvementCommentRecommendService.recommendImprovementComment(improvementCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 추천 성공",
                null
        ));
    }

    /**
     * 개선요청 댓글 추천 삭제
     */
    @DeleteMapping("/comment/{improvementCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long improvementCommentId) {
        improvementCommentRecommendService.deleteRecommendImprovementComment(improvementCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 추천 삭제",
                null
        ));
    }

    /**
     * 개선요청 대댓글 추천
     */
    @PostMapping("/reply/{improvementReplyId}")
    public ResponseEntity<ResponseDto<Void>> recommendReply(@PathVariable Long improvementReplyId) {
        improvementReplyRecommendService.recommendImprovementReply(improvementReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 대댓글 추천 성공",
                null
        ));
    }

    /**
     * 개선요청 대댓글 추천 삭제
     */
    @DeleteMapping("/reply/{improvementReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteReply(@PathVariable Long improvementReplyId) {
        improvementReplyRecommendService.deleteRecommendImprovementReply(improvementReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 대댓글 추천 삭제",
                null
        ));
    }
}
