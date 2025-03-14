package org.myteam.server.comment.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest.*;
import org.myteam.server.comment.dto.response.CommentResponse.*;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.comment.util.CommentFactory;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentFactory commentFactory;
    private final SecurityReadService securityReadService;
    private final BadWordFilter badWordFilter;
    private final S3Service s3Service;
    private final MemberJpaRepository memberJpaRepository;
    private final CommentReadService commentReadService;
    private final CommentQueryRepository commentQueryRepository;
    private Map<CommentType, CommentCountService> countServiceMap;

    @PostConstruct
    public void init(Map<String, CommentCountService> countServices) {
        this.countServiceMap = countServices.entrySet().stream()
                .collect(Collectors.toMap(entry ->
                        CommentType.valueOf(
                                entry.getKey().replace("CommentCountService", "").toUpperCase()
                        ), Map.Entry::getValue)
                );
    }

    /**
     * 댓글 작성
     */
    public CommentSaveResponse addComment(Long contentId, CommentSaveRequest request, String createdIp) {
        log.info("댓글 작성 요청 - type: {}, contentId: {}, memberId: {}, parentId: {}",
                request.getType(), contentId, securityReadService.getMember().getPublicId(), request.getParentId());

        Member member = securityReadService.getMember();
        Member mentionedMember = memberJpaRepository.findByPublicId(request.getMentionedPublicId())
                .orElseThrow(() -> {
                    log.error("댓글 작성 실패 - 멘션된 사용자({})를 찾을 수 없음", request.getMentionedPublicId());
                    return new PlayHiveException(ErrorCode.USER_NOT_FOUND);
                });

        // 팩토리 패턴을 통해서 comment 생성
        Comment comment = commentFactory.createComment(request.getType(), contentId, member, mentionedMember,
                badWordFilter.filterMessage(request.getComment()), request.getImageUrl(), createdIp, request.getParentId());

        commentRepository.save(comment);
        log.info("댓글 작성 완료 - commentId: {}, contentId: {}, 작성자: {}", comment.getId(), contentId, member.getPublicId());

        // 댓글 카운트 증가
        CommentCountService countService = countServiceMap.get(request.getType());
        if (countService == null) {
            throw new PlayHiveException(ErrorCode.NOT_SUPPORT_COMMENT_TYPE);
        }
        countService.addCommentCount(contentId);

        // TODO: 추천 반영

        return CommentSaveResponse.createResponse(comment, member, mentionedMember);
    }

    /**
     * 댓글 수정
     */
    public CommentSaveResponse update(Long commentId, CommentSaveRequest request) {
        log.info("댓글 수정 요청 - commentId: {}, memberId: {}", commentId, securityReadService.getMember().getPublicId());

        Member member = securityReadService.getMember();
        Member mentionedMember = memberJpaRepository.findByPublicId(request.getMentionedPublicId())
                .orElseThrow(() -> {
                    log.error("댓글 수정 실패 - 멘션된 사용자({})를 찾을 수 없음", request.getMentionedPublicId());
                    return new PlayHiveException(ErrorCode.USER_NOT_FOUND);
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

        comment.updateComment(request.getImageUrl(), request.getComment());
        commentRepository.save(comment);
        log.info("댓글 수정 완료 - commentId: {}, memberId: {}", commentId, member.getPublicId());

        // TODO: 추천 반영

        return CommentSaveResponse.createResponse(comment, member, mentionedMember);
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(Long contentId, CommentDeleteRequest request) {
        Long commentId = request.getCommentId();
        Member member = securityReadService.getMember();
        Comment comment = commentReadService.findById(commentId);

        if (!comment.verifyCommentAuthor(member)) {
            log.warn("댓글 삭제 실패 - 작성자 불일치 (commentId: {}, 요청자: {}, 작성자: {})",
                    commentId, member.getPublicId(), comment.getMember().getPublicId());
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }

        if (comment.getImageUrl() != null) {
            log.info("이미지 삭제 - commentId: {}, 이미지: {}", commentId, comment.getImageUrl());
            s3Service.deleteFile(MediaUtils.getImagePath(comment.getImageUrl()));
        }

        // TODO: 댓글 추천 삭제

        // 대댓글 + 부모 댓글 삭제
        // 이미지 있는 댓글 조회
        List<Comment> commentsWithImages = commentQueryRepository.findRepliesWithImages(commentId);

        // S3에서 이미지 삭제
        for (Comment reply : commentsWithImages) {
            log.info("S3 이미지 삭제 - commentId: {}, 이미지: {}", reply.getId(), reply.getImageUrl());
            s3Service.deleteFile(MediaUtils.getImagePath(reply.getImageUrl()));
        }

        int minusCount = commentQueryRepository.deleteReply(request.getCommentId());

        log.info("댓글 삭제 완료 - commentId: {}, 요청자: {}", commentId, member.getPublicId());

        // 댓글 카운트 감소
        CommentCountService countService = countServiceMap.get(request.getType());
        if (countService == null) {
            throw new PlayHiveException(ErrorCode.NOT_SUPPORT_COMMENT_TYPE);
        }
        countService.minusCommentCount(contentId, minusCount);
    }
}

