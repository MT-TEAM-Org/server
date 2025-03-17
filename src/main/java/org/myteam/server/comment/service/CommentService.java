package org.myteam.server.comment.service;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest.*;
import org.myteam.server.comment.dto.response.CommentResponse.*;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.comment.repository.CommentRecommendRepository;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.comment.util.CommentFactory;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final CommentRecommendReadService commentRecommendReadService;
    private final CommentRecommendRepository commentRecommendRepository;
    private Map<CommentType, CommentCountService> countServiceMap;

    @Autowired
    public CommentService(Map<String, CommentCountService> countServices,
                          CommentRepository commentRepository,
                          CommentFactory commentFactory,
                          SecurityReadService securityReadService,
                          BadWordFilter badWordFilter,
                          S3Service s3Service,
                          MemberJpaRepository memberJpaRepository,
                          CommentReadService commentReadService,
                          CommentRecommendReadService commentRecommendReadService,
                          CommentRecommendRepository commentRecommendRepository,
                          CommentQueryRepository commentQueryRepository) {

        this.commentRepository = commentRepository;
        this.commentFactory = commentFactory;
        this.securityReadService = securityReadService;
        this.badWordFilter = badWordFilter;
        this.s3Service = s3Service;
        this.memberJpaRepository = memberJpaRepository;
        this.commentReadService = commentReadService;
        this.commentQueryRepository = commentQueryRepository;
        this.commentRecommendReadService = commentRecommendReadService;
        this.commentRecommendRepository = commentRecommendRepository;

        log.info("등록된 CommentCountService Bean 목록: {}", countServices.keySet());

        this.countServiceMap = countServices.entrySet().stream()
                .peek(entry -> log.info("처리 중: Bean 이름={}, 변환 결과={}",
                        entry.getKey(), convertBeanNameToEnum(entry.getKey())))
                .collect(Collectors.toMap(
                        entry -> {
                            String convertedName = convertBeanNameToEnum(entry.getKey());
                            try {
                                return CommentType.valueOf(convertedName);
                            } catch (IllegalArgumentException e) {
                                log.error("잘못된 Bean 이름: {}, 변환 실패 (변환 값: {})", entry.getKey(), convertedName, e);
                                throw e;
                            }
                        },
                        Map.Entry::getValue
                ));
    }

    private String convertBeanNameToEnum(String beanName) {
        int startIdx = beanName.indexOf("CountService");
        String typeName = beanName.substring(0, startIdx);
        return toUpperSnakeCase(typeName);
    }

    /**
     * PascalCase를 UPPER_SNAKE_CASE로 변환하는 유틸리티 메서드
     * "BoardCommentCountService" -> "BOARD"
     */
    private String toUpperSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase()
                .replace("_COMMENT_COUNT_SERVICE", ""); // 불필요한 "_COMMENT_COUNT_SERVICE" 제거
    }

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
                badWordFilter.filterMessage(request.getComment()), request.getImageUrl(), createdIp, request.getParentId());

        commentRepository.save(comment);
        log.info("댓글 작성 완료 - commentId: {}, contentId: {}, 작성자: {}", comment.getId(), contentId, member.getPublicId());

        // 댓글 카운트 증가
        CommentCountService countService = countServiceMap.get(request.getType());
        if (countService == null) {
            throw new PlayHiveException(ErrorCode.NOT_SUPPORT_COMMENT_TYPE);
        }
        countService.addCommentCount(contentId);

        boolean isRecommended = commentRecommendReadService.isRecommended(comment.getId(), member.getPublicId());

        CommentSaveResponse response = CommentSaveResponse.createResponse(comment);
        response.setRecommended(isRecommended);

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

        CommentSaveResponse response = CommentSaveResponse.createResponse(comment);
        response.setRecommended(isRecommended);

        return response;
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

        commentRecommendRepository.deleteByCommentIdAndMemberPublicId(comment.getId(), member.getPublicId());

        // 대댓글 + 부모 댓글 삭제
        // 이미지 있는 댓글 조회
        List<Comment> commentsWithImages = getReply(commentId, member.getPublicId());

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

    private List<Comment> getReply(Long commentId, UUID loginUser) {
        List<Comment> comments = commentQueryRepository.findRepliesWithImages(commentId);
        for (Comment reply : comments) {
            commentRecommendRepository.deleteByCommentIdAndMemberPublicId(reply.getId(), loginUser);
        }

        return comments;
    }
}

