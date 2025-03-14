package org.myteam.server.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.dto.request.CommentRequest.*;
import org.myteam.server.comment.dto.response.CommentResponse.*;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public CommentSaveListResponse getComments(CommentListRequest request) {
        log.info("댓글 목록 조회 요청 - type: {}, contentId: {}, page: {}, size: {}",
                request.getType(), request.getContentId(),
                request.getPage(), request.getSize());

        List<CommentSaveResponse> list = commentQueryRepository.getCommentList(
                request.getType(),
                request.getContentId(),
                request.toServiceRequest().toPageable()
        );

        log.info("댓글 목록 조회 완료 - contentId: {}, 조회된 댓글 수: {}", request.getContentId(), list.size());

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

    public CommentSaveListResponse getBestComments(CommentListRequest request) {
        log.info("베스트 댓글 목록 조회 요청 - type: {}, contentId: {}, page: {}, size: {}",
                request.getType(), request.getContentId(),
                request.getPage(), request.getSize());

        List<CommentSaveResponse> list = commentQueryRepository.getBestCommentList(
                request.getType(),
                request.getContentId(),
                request.toServiceRequest().toPageable()
        );

        log.info("베스트 댓글 목록 조회 완료 - contentId: {}, 조회된 댓글 수: {}", request.getContentId(), list.size());

        return CommentSaveListResponse.createResponse(list);
    }
}
