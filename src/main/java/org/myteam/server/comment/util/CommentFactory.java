package org.myteam.server.comment.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.comment.domain.*;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.service.ImprovementReadService;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.notice.repository.NoticeRepository;
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

    public Comment createComment(CommentType type, Long contentId, Member member, Member mentionedMember, String comment, String imageUrl, String createdIp, Long parentId) {
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

            if (!parent.canAddReply()) {
                log.warn("댓글 생성 실패 - 최대 대댓글 깊이 초과 (parentId: {}, depth: {})",
                        parentId, parent.getDepth());
                throw new PlayHiveException(ErrorCode.LIMIT_COMMENT_DEPTH);
            }
        }

        // 댓글 타입에 따라 생성
        return switch (type) {
            case BOARD -> BoardComment.createComment(
                    boardReadService.findById(contentId),
                    member,
                    mentionedMember,
                    comment,
                    imageUrl,
                    createdIp,
                    parent
            );

            case NEWS -> NewsComment.createComment(
                    newsReadService.findById(contentId),
                    member,
                    mentionedMember,
                    comment,
                    imageUrl,
                    createdIp,
                    parent
            );

            case IMPROVEMENT -> ImprovementComment.createComment(
                    improvementReadService.findById(contentId),
                    member,
                    mentionedMember,
                    comment,
                    imageUrl,
                    createdIp,
                    parent
            );

            case INQUIRY -> InquiryComment.createComment(
                    inquiryReadService.findInquiryById(contentId),
                    member,
                    mentionedMember,
                    comment,
                    imageUrl,
                    createdIp,
                    parent
            );

            case NOTICE -> NoticeComment.createComment(
                    noticeReadService.findById(contentId),
                    member,
                    mentionedMember,
                    comment,
                    imageUrl,
                    createdIp,
                    parent
            ) ;
        };
    }
}
