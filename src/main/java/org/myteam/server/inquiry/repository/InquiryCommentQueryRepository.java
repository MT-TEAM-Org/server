package org.myteam.server.inquiry.repository;

import static org.myteam.server.inquiry.domain.QInquiryComment.inquiryComment;
import static org.myteam.server.inquiry.domain.QInquiryReply.inquiryReply;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InquiryCommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<InquiryReplyResponse> getRepliesForComments(Long inquiryCommentId) {
        List<InquiryReplyResponse> replies = queryFactory
                .select(Projections.fields(InquiryReplyResponse.class,
                        inquiryReply.inquiryComment.id.as("inquiryCommentId"),
                        inquiryReply.id.as("inquiryReplyId"),
                        inquiryReply.createdIp,
                        inquiryReply.member.publicId,
                        inquiryReply.member.nickname,
                        inquiryReply.imageUrl,
                        inquiryReply.comment,
                        inquiryReply.recommendCount,
                        inquiryReply.mentionedMember.publicId.as("mentionedPublicId"),
                        inquiryReply.mentionedMember.nickname.as("mentionedNickname"),
                        inquiryReply.createDate,
                        inquiryReply.lastModifiedDate))
                .from(inquiryReply)
                .leftJoin(inquiryReply.mentionedMember)
                .where(isInquiryCommentEqualTo(inquiryCommentId))
                .orderBy(inquiryReply.createDate.desc())
                .fetch();

        return replies;
    }

    public int getCommentCountByPublicId(UUID publicId) {
        return queryFactory
                .select(inquiryComment.count())
                .from(inquiryComment)
                .where(inquiryComment.member.publicId.eq(publicId))
                .fetchOne()
                .intValue();
    }

    public int getReplyCountByPublicId(UUID publicId) {
        return queryFactory
                .select(inquiryReply.count())
                .from(inquiryReply)
                .where(inquiryReply.member.publicId.eq(publicId))
                .fetchOne()
                .intValue();
    }

    private BooleanExpression isInquiryCommentEqualTo(Long inquiryCommentId) {
        return inquiryReply.inquiryComment.id.eq(inquiryCommentId);
    }
}
