package org.myteam.server.notice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.notice.service.NoticeCommentRecommendService;
import org.myteam.server.notice.service.NoticeCountService;
import org.myteam.server.notice.service.NoticeReplyRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/recommend/notice")
@RequiredArgsConstructor
public class NoticeRecommendController {

    private final NoticeCountService noticeCountService;
    private final NoticeCommentRecommendService noticeCommentRecommendService;
    private final NoticeReplyRecommendService noticeReplyRecommendService;

    /**
     * 공지사항 추천
     */
    @PostMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<Void>> recommendNotice(@PathVariable Long noticeId) {
        noticeCountService.recommendNotice(noticeId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 추천 성공",
                null));
    }

    /**
     * 공지사항 추천 삭제
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<Void>> deleteNotice(@PathVariable Long noticeId) {
        noticeCountService.deleteRecommendNotice(noticeId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 추천 삭제 성공",
                null
        ));
    }

    /**
     * 공지사항 댓글 추천
     */
    @PostMapping("/comment/{noticeCommentId}")
    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long noticeCommentId) {
        noticeCommentRecommendService.recommendNoticeComment(noticeCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 추천 성공",
                null
        ));
    }

    /**
     * 공지사항 댓글 추천 삭제
     */
    @DeleteMapping("/comment/{noticeCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long noticeCommentId) {
        noticeCommentRecommendService.deleteRecommendNoticeComment(noticeCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 추천 삭제",
                null
        ));
    }

    /**
     * 공지사항 댓글 추천
     */
    @PostMapping("/reply/{noticeReplyId}")
    public ResponseEntity<ResponseDto<Void>> recommendReply(@PathVariable Long noticeReplyId) {
        noticeReplyRecommendService.recommendNoticeReply(noticeReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 대댓글 추천 성공",
                null
        ));
    }

    /**
     * 공지사항 댓글 추천 삭제
     */
    @DeleteMapping("/reply/{noticeReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteReply(@PathVariable Long noticeReplyId) {
        noticeReplyRecommendService.deleteRecommendNoticeReply(noticeReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 대댓글 추천 삭제",
                null
        ));
    }
}
