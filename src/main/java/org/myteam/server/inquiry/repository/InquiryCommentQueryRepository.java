package org.myteam.server.inquiry.repository;

import static org.myteam.server.inquiry.domain.QInquiryReply.inquiryReply;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.inquiry.dto.response.InquiryReplyResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    private BooleanExpression isInquiryCommentEqualTo(Long inquiryCommentId) {
        return inquiryReply.inquiryComment.id.eq(inquiryCommentId);
    }
}
