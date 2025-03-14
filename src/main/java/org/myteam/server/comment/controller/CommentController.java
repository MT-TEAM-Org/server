package org.myteam.server.comment.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.domain.NoticeComment;
import org.myteam.server.comment.dto.request.CommentRequest.*;
import org.myteam.server.comment.dto.response.CommentResponse.*;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentReadService commentReadService;

    /**
     * 공지사항 댓글 작성 API
     */
    @PostMapping("/{contentId}/comment")
    public ResponseEntity<ResponseDto<CommentSaveResponse>> addComment(@PathVariable Long contentId,
                                                                       @Valid @RequestBody CommentSaveRequest request,
                                                                       HttpServletRequest httpServletRequest) {

        CommentSaveResponse response = commentService.addComment(contentId, request, ClientUtils.getRemoteIP(httpServletRequest));
        return ResponseEntity.ok(new ResponseDto<>(
                    SUCCESS.name(),
                    "댓글이 생성되었습니다.",
                    response
                ));
    }

    /**
     * 댓글 수정 API
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto<CommentSaveResponse>> updateComment(@PathVariable Long commentId,
                                                                          @Valid @RequestBody CommentSaveRequest request) {

        CommentSaveResponse response = commentService.update(commentId, request);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글이 수정되었습니다.",
                response
        ));
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/{contentId}/comment")
    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long contentId,
                                                           @Valid @RequestBody CommentDeleteRequest request) {

        commentService.deleteComment(contentId, request);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글이 삭제되었습니다.",
                null
        ));
    }

    /**
     * 댓글 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ResponseDto<CommentSaveListResponse>> getComments(@Valid @ModelAttribute CommentListRequest request) {
        CommentSaveListResponse response = commentReadService.getComments(request);

        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "댓글 목록 조회 성공",
                response
        ));
    }

    /**
     * 댓글 상세 조회
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseDto<CommentSaveResponse>> getCommentDetail(@PathVariable Long commentId) {
        CommentSaveResponse comment = commentReadService.getCommentDetail(commentId);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "댓글 상세 조회 성공",
                comment
        ));
    }

    /**
     * 베스트 댓글 목록 조회
     */
    @GetMapping("/best")
    public ResponseEntity<ResponseDto<CommentSaveListResponse>> getBestComments(@Valid @ModelAttribute CommentListRequest request) {
        CommentSaveListResponse bestComments = commentReadService.getBestComments(request);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "베스트 댓글 조회 성공",
                bestComments
        ));
    }

//    /**
//     * 특정 공지사항의 댓글 목록 조회 API (페이징 적용)
//     */
//    @GetMapping("/{noticeId}")
//    public ResponseEntity<Page<NoticeComment>> getNoticeComments(
//            @PathVariable Long noticeId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
//        Page<NoticeComment> comments = commentService.getNoticeComments(noticeId, pageable);
//        return ResponseEntity.ok(comments);
//    }
//
//    /**
//     * 내가 작성한 공지사항 댓글 조회 API
//     */
//    @GetMapping("/my-comments")
//    public ResponseEntity<Page<NoticeComment>> getMyComments(
//            @RequestParam UUID memberId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<NoticeComment> comments = commentService.getMyNoticeComments(memberId, pageable);
//        return ResponseEntity.ok(comments);
//    }
}
