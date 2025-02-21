package org.myteam.server.notice.Repository;

import static org.myteam.server.notice.domain.QNoticeComment.noticeComment;
import static org.myteam.server.notice.domain.QNoticeReply.noticeReply;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.notice.dto.response.NoticeCommentResponse.*;
import org.myteam.server.notice.service.NoticeCommentRecommendReadService;
import org.myteam.server.notice.service.NoticeReplyRecommendReadService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NoticeCommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final NoticeCommentRecommendReadService noticeCommentRecommendReadService;
    private final NoticeReplyRecommendReadService noticeReplyRecommendReadService;

    public List<NoticeCommentSaveResponse> getNoticeCommentList(Long noticeId, CustomUserDetails userDetails) {
        List<NoticeCommentSaveResponse> list = queryFactory
                .select(Projections.constructor(NoticeCommentSaveResponse.class,
                        noticeComment.id,
                        noticeComment.notice.id,
                        noticeComment.createdIp,
                        noticeComment.member.publicId,
                        noticeComment.member.nickname,
                        noticeComment.imageUrl,
                        noticeComment.comment,
                        noticeComment.recommendCount,
                        noticeComment.createDate,
                        noticeComment.lastModifiedDate,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended")
                ))
                .from(noticeComment)
                .where(isNoticeEqualTo(noticeId))
                .orderBy(noticeComment.createDate.desc())
                .fetch();

        list.forEach(comment -> {
            boolean isRecommended = false;

            if (userDetails != null) {
                UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
                isRecommended = noticeCommentRecommendReadService.isRecommended(comment.getNoticeCommentId(), loginUser);
            }
            comment.setRecommended(isRecommended);
            comment.setNoticeReplyList(getRepliesForComments(comment.getNoticeCommentId(), userDetails));
        });

        return list;
    }

    public List<NoticeReplyResponse> getRepliesForComments(Long noticeCommentId, CustomUserDetails userDetails) {
        List<NoticeReplyResponse> replies = queryFactory
                .select(Projections.fields(NoticeReplyResponse.class,
                        noticeReply.noticeComment.id.as("noticeCommentId"),
                        noticeReply.id.as("noticeReplyId"),
                        noticeReply.createdIp,
                        noticeReply.member.publicId,
                        noticeReply.member.nickname,
                        noticeReply.imageUrl,
                        noticeReply.comment,
                        noticeReply.recommendCount,
                        noticeReply.mentionedMember.publicId.as("mentionedPublicId"),
                        noticeReply.mentionedMember.nickname.as("mentionedNickname"),
                        noticeReply.createDate,
                        noticeReply.lastModifiedDate
                ))
                .from(noticeReply)
                .leftJoin(noticeReply.mentionedMember)
                .where(isNoticeEqualTo(noticeCommentId))
                .orderBy(noticeReply.createDate.desc())
                .fetch();

        replies.forEach(reply -> {
            boolean isRecommended = false;

            if (userDetails != null) {
                UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
                isRecommended = noticeReplyRecommendReadService.isRecommended(reply.getNoticeReplyId(), loginUser);
            }
            reply.setRecommended(isRecommended);
        });

        return replies;
    }

    private BooleanExpression isNoticeEqualTo(Long noticeId) {
        return noticeComment.notice.id.eq(noticeId);
    }
}
