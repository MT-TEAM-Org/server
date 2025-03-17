package org.myteam.server.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.dto.request.CommentRequest.CommentListRequest;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveListResponse;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveResponse;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentReadService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final SecurityReadService securityReadService;

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public CommentSaveListResponse getComments(Long contentId, CommentListRequest request) {
        log.info("댓글 목록 조회 요청 - type: {}, contentId: {}, page: {}, size: {}",
                request.getType(), contentId,
                request.getPage(), request.getSize());

        List<CommentSaveResponse> list = commentQueryRepository.getCommentList(
                request.getType(),
                contentId,
                request.toServiceRequest().toPageable()
        );

        log.info("댓글 목록 조회 완료 - contentId: {}, 조회된 댓글 수: {}", contentId, list.size());

        return CommentSaveListResponse.createResponse(list);
    }

    public CommentSaveResponse getCommentDetail(Long commentId) {
        log.info("댓글 상세 조회 요청 - commentId: {}", commentId);

        Comment comment = findById(commentId);

        CommentSaveResponse response = CommentSaveResponse.createResponse(comment);
        commentQueryRepository.getCommentReply(response);

        log.info("댓글 상세 조회 완료 - commentId: {}, 작성자: {}, 추천수: {}",
                response.getCommentId(), response.getNickname(), response.getRecommendCount());

        return response;
    }

    public CommentSaveListResponse getBestComments(Long contentId, CommentListRequest request) {
        log.info("베스트 댓글 목록 조회 요청 - type: {}, contentId: {}, page: {}, size: {}",
                request.getType(), contentId,
                request.getPage(), request.getSize());

        List<CommentSaveResponse> list = commentQueryRepository.getBestCommentList(
                request.getType(),
                contentId,
                request.toServiceRequest().toPageable()
        );

        log.info("베스트 댓글 목록 조회 완료 - contentId: {}, 조회된 댓글 수: {}", contentId, list.size());

        return CommentSaveListResponse.createResponse(list);
    }
}
