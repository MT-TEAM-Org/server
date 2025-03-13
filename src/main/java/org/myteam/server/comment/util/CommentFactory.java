package org.myteam.server.comment.util;

import lombok.RequiredArgsConstructor;
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
        Comment parent = null;

        if (parentId != null) {
            // 부모 댓글 조회 (부모 댓글이 존재하면 참조)
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new PlayHiveException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parent.canAddReply()) {
                throw new PlayHiveException(ErrorCode.LIMIT_COMMENT_DEPTH);
            }
        }

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
