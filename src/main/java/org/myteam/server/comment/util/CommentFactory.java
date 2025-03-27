package org.myteam.server.comment.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.comment.domain.BoardComment;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.ImprovementComment;
import org.myteam.server.comment.domain.InquiryComment;
import org.myteam.server.comment.domain.NewsComment;
import org.myteam.server.comment.domain.NoticeComment;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.service.ImprovementReadService;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.notice.service.NoticeReadService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentFactory {
    private final CommentRepository commentRepository;
    private final NoticeReadService noticeReadService;
    private final BoardReadService boardReadService;
    private final NewsReadService newsReadService;
    private final ImprovementReadService improvementReadService;
    private final InquiryReadService inquiryReadService;
    private final InquiryRepository inquiryRepository;

    public Comment createComment(CommentType type, Long contentId, Member member, Member mentionedMember,
                                 String comment, String imageUrl, String createdIp, Long parentId) {
        log.info("댓글 생성 요청 - type: {}, contentId: {}, memberId: {}, mentionedMemberId: {}, parentId: {}",
                type, contentId, member.getPublicId(),
                (mentionedMember != null ? mentionedMember.getPublicId() : "N/A"),
                parentId);

        Comment parent = null;

        if (parentId != null) {
            // 부모 댓글 조회 (부모 댓글이 존재하면 참조)
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> {
                        log.error("댓글 생성 실패 - 부모 댓글({})을 찾을 수 없음", parentId);
                        return new PlayHiveException(ErrorCode.COMMENT_NOT_FOUND);
                    });

            // 부모 댓글의 타입과 현재 댓글 타입이 일치하는지 검사
            if (!parent.getCommentType().equals(type)) {
                log.error("댓글 생성 실패 - 부모 댓글의 타입({})과 요청된 타입({})이 일치하지 않음",
                        parent.getCommentType(), type);
                throw new PlayHiveException(ErrorCode.COMMENT_TYPE_MISMATCH);
            }

            if (!parent.canAddReply()) {
                log.warn("댓글 생성 실패 - 최대 대댓글 깊이 초과 (parentId: {}, depth: {})",
                        parentId, parent.getDepth());
                throw new PlayHiveException(ErrorCode.LIMIT_COMMENT_DEPTH);
            }
        }

        // 댓글 타입에 따라 생성
        switch (type) {
            case BOARD:
                return BoardComment.createComment(
                        boardReadService.findById(contentId),
                        member,
                        mentionedMember,
                        comment,
                        imageUrl,
                        createdIp,
                        parent
                );

            case NEWS:
                return NewsComment.createComment(
                        newsReadService.findById(contentId),
                        member,
                        mentionedMember,
                        comment,
                        imageUrl,
                        createdIp,
                        parent
                );

            case IMPROVEMENT:
                return ImprovementComment.createComment(
                        improvementReadService.findById(contentId),
                        member,
                        mentionedMember,
                        comment,
                        imageUrl,
                        createdIp,
                        parent
                );

            case INQUIRY:
                Inquiry inquiry = inquiryReadService.findInquiryById(contentId);
                if (member.isAdmin()) {
                    inquiry.updateAdminAnswered();
                    inquiryRepository.save(inquiry);
                }
                return InquiryComment.createComment(
                        inquiry,
                        member,
                        mentionedMember,
                        comment,
                        imageUrl,
                        createdIp,
                        parent
                );

            case NOTICE:
                return NoticeComment.createComment(
                        noticeReadService.findById(contentId),
                        member,
                        mentionedMember,
                        comment,
                        imageUrl,
                        createdIp,
                        parent
                );

            default:
                throw new PlayHiveException(ErrorCode.NOT_SUPPORT_COMMENT_TYPE);
        }

    }
}
