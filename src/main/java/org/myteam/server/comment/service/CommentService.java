package org.myteam.server.comment.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest.CommentDeleteRequest;
import org.myteam.server.comment.dto.request.CommentRequest.CommentSaveRequest;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveResponse;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.comment.repository.CommentRecommendRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.comment.util.CommentFactory;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.upload.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentFactory commentFactory;
    private final SecurityReadService securityReadService;
    private final BadWordFilter badWordFilter;
    private final StorageService s3Service;
    private final MemberJpaRepository memberJpaRepository;
    private final CommentReadService commentReadService;
    private final CommentQueryRepository commentQueryRepository;
    private final CommentRecommendReadService commentRecommendReadService;
    private final CommentRecommendRepository commentRecommendRepository;
    private final CommentRecommendService commentRecommendService;
    private final RedisCountService redisCountService;

    /**
     * 댓글 작성
     */
    public CommentSaveResponse addComment(Long contentId, CommentSaveRequest request, String createdIp) {
        log.info("댓글 작성 요청 - type: {}, contentId: {}, memberId: {}, parentId: {}",
                request.getType(), contentId, securityReadService.getMember().getPublicId(), request.getParentId());

        Member member = securityReadService.getMember();
        Member mentionedMember = memberJpaRepository.findByPublicId(request.getMentionedPublicId())
                .orElseGet(() -> {
                    log.info("멘션된 사용자({})를 찾을 수 없음", request.getMentionedPublicId());
                    return null;
                });

        // 팩토리 패턴을 통해서 comment 생성
        Comment comment = commentFactory.createComment(request.getType(), contentId, member, mentionedMember,
                badWordFilter.filterMessage(request.getComment()), request.getImageUrl(), createdIp,
                request.getParentId());

        commentRepository.save(comment);
        log.info("댓글 작성 완료 - commentId: {}, contentId: {}, 작성자: {}", comment.getId(), contentId, member.getPublicId());

        // 댓글 카운트 증가
        if (!request.getType().equals(CommentType.MATCH)) {
            // 댓글수 증가
            redisCountService.getCommonCount(ServiceType.COMMENT, DomainType.changeType(request.getType()), contentId,
                    null);
        }

        CommentSaveResponse response = CommentSaveResponse.createResponse(comment, false);

        return response;
    }

    /**
     * 댓글 수정
     */
    public CommentSaveResponse update(Long commentId, CommentSaveRequest request) {
        log.info("댓글 수정 요청 - commentId: {}, memberId: {}", commentId, securityReadService.getMember().getPublicId());

        Member member = securityReadService.getMember();
        Member mentionedMember = memberJpaRepository.findByPublicId(request.getMentionedPublicId())
                .orElseGet(() -> {
                    log.info("멘션된 사용자({})를 찾을 수 없음", request.getMentionedPublicId());
                    return null;
                });
        Comment comment = commentReadService.findById(commentId);

        if (!comment.verifyCommentAuthor(member)) {
            log.warn("댓글 수정 실패 - 작성자 불일치 (commentId: {}, 요청자: {}, 작성자: {})",
                    commentId, member.getPublicId(), comment.getMember().getPublicId());
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }

        if (MediaUtils.verifyImageUrlAndRequestImageUrl(comment.getImageUrl(), request.getImageUrl())) {
            log.info("기존 이미지 삭제 - commentId: {}, 기존 이미지: {}", commentId, comment.getImageUrl());
            s3Service.deleteFile(MediaUtils.getImagePath(comment.getImageUrl()));
        }

        comment.updateComment(request.getImageUrl(), request.getComment(), mentionedMember);
        commentRepository.save(comment);
        log.info("댓글 수정 완료 - commentId: {}, memberId: {}", commentId, member.getPublicId());

        boolean isRecommended = commentRecommendReadService.isRecommended(comment.getId(), member.getPublicId());

        CommentSaveResponse response = CommentSaveResponse.createResponse(comment, isRecommended);

        return response;
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(Long contentId, Long commentId, CommentDeleteRequest request) {
        Member member = securityReadService.getMember();
        Comment comment = commentReadService.findByIdAndCommentType(commentId, request.getType());

        if (!comment.verifyCommentAuthor(member)) {
            log.warn("댓글 삭제 실패 - 작성자 불일치 (commentId: {}, 요청자: {}, 작성자: {})",
                    commentId, member.getPublicId(), comment.getMember().getPublicId());
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }

        if (comment.getImageUrl() != null) {
            log.info("이미지 삭제 - commentId: {}, 이미지: {}", commentId, comment.getImageUrl());
            s3Service.deleteFile(MediaUtils.getImagePath(comment.getImageUrl()));
        }

        commentRecommendRepository.deleteByCommentIdAndMemberPublicId(comment.getId(), member.getPublicId());

        // 대댓글 + 부모 댓글 삭제
        // 이미지 있는 댓글 조회
        List<Comment> commentsWithImages = getReply(commentId, member.getPublicId());

        // S3에서 이미지 삭제
        for (Comment reply : commentsWithImages) {
            log.info("S3 이미지 삭제 - commentId: {}, 이미지: {}", reply.getId(), reply.getImageUrl());
            s3Service.deleteFile(MediaUtils.getImagePath(reply.getImageUrl()));
        }

        int minusCount = commentQueryRepository.deleteReply(commentId);

        log.info("댓글 삭제 완료 - commentId: {}, 요청자: {}", commentId, member.getPublicId());

        // 댓글 카운트 감소
        if (!request.getType().equals(CommentType.MATCH)) {
            redisCountService.getCommonCount(ServiceType.COMMENT_REMOVE, DomainType.changeType(request.getType()),
                    contentId, minusCount);
        }
    }

    public void deleteCommentByPost(CommentType commentType, Long contentId) {
        List<Comment> list = commentQueryRepository.getCommentList(
                commentType,
                contentId
        );

        for (Comment comment : list) {
            commentRecommendService.deleteCommentRecommendByPost(comment.getId());
            if (comment.getImageUrl() != null) {
                s3Service.deleteFile(comment.getImageUrl());
            }
        }

        commentRepository.deleteAll(list);
    }

    private List<Comment> getReply(Long commentId, UUID loginUser) {
        List<Comment> comments = commentQueryRepository.findRepliesWithImages(commentId);
        for (Comment reply : comments) {
            commentRecommendRepository.deleteByCommentIdAndMemberPublicId(reply.getId(), loginUser);
        }

        return comments;
    }
}

