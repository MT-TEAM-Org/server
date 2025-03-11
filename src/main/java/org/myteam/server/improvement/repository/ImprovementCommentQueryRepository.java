package org.myteam.server.improvement.repository;

import static org.myteam.server.improvement.domain.QImprovementComment.improvementComment;
import static org.myteam.server.improvement.domain.QImprovementReply.improvementReply;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.improvement.domain.ImprovementOrderType;
import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
import org.myteam.server.improvement.service.ImprovementCommentRecommendReadService;
import org.myteam.server.improvement.service.ImprovementReplyRecommendReadService;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.util.ClientUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ImprovementCommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final ImprovementCommentRecommendReadService improvementCommentRecommendReadService;
    private final ImprovementReplyRecommendReadService improvementReplyRecommendReadService;

    public List<ImprovementCommentSaveResponse> getImprovementCommentList(Long improvementId,
                                                                          ImprovementOrderType improvementOrderType,
                                                                          UUID loginUser) {
        List<ImprovementCommentSaveResponse> list = queryFactory
                .select(Projections.constructor(ImprovementCommentSaveResponse.class,
                        improvementComment.id,
                        improvementComment.improvement.id,
                        improvementComment.createdIp,
                        improvementComment.member.publicId,
                        improvementComment.member.nickname,
                        improvementComment.imageUrl,
                        improvementComment.comment,
                        improvementComment.recommendCount,
                        improvementComment.createDate,
                        improvementComment.lastModifiedDate,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended")
                ))
                .from(improvementComment)
                .where(isImprovementEqualTo(improvementId))
                .orderBy(isOrderByEqualToOrderType(improvementOrderType))
                .fetch();

        list.forEach(comment -> {
            boolean isRecommended = false;

            if (loginUser != null) {
                isRecommended = improvementCommentRecommendReadService.isRecommended(comment.getImprovementCommentId(), loginUser);
            }
            comment.setCreatedIp(ClientUtils.maskIp(comment.getCreatedIp()));
            comment.setRecommended(isRecommended);
            comment.setImprovementReplyList(getRepliesForComments(comment.getImprovementCommentId(), loginUser));
        });

        return list;
    }

    public List<ImprovementReplyResponse> getRepliesForComments(Long improvementCommentId, UUID loginUser) {
        List<ImprovementReplyResponse> replies = queryFactory
                .select(Projections.fields(ImprovementReplyResponse.class,
                        improvementReply.improvementComment.id.as("improvementCommentId"),
                        improvementReply.id.as("improvementReplyId"),
                        improvementReply.createdIp,
                        improvementReply.member.publicId,
                        improvementReply.member.nickname,
                        improvementReply.imageUrl,
                        improvementReply.comment,
                        improvementReply.recommendCount,
                        improvementReply.mentionedMember.publicId.as("mentionedPublicId"),
                        improvementReply.mentionedMember.nickname.as("mentionedNickname"),
                        improvementReply.createDate,
                        improvementReply.lastModifiedDate
                ))
                .from(improvementReply)
                .leftJoin(improvementReply.mentionedMember)
                .where(isImprovementCommentEqualTo(improvementCommentId))
                .orderBy(improvementReply.createDate.desc())
                .fetch();

        replies.forEach(reply -> {
            boolean isRecommended = false;

            if (loginUser != null) {
                isRecommended = improvementReplyRecommendReadService.isRecommended(reply.getImprovementReplyId(), loginUser);
            }
            reply.setCreatedIp(ClientUtils.maskIp(reply.getCreatedIp()));
            reply.setRecommended(isRecommended);
        });

        return replies;
    }

    private BooleanExpression isImprovementCommentEqualTo(Long improvementCommentId) {
        return improvementReply.improvementComment.id.eq(improvementCommentId);
    }

    private OrderSpecifier<?> isOrderByEqualToOrderType(ImprovementOrderType orderType) {
        // default 추천순
        ImprovementOrderType order = Optional.ofNullable(orderType).orElse(ImprovementOrderType.RECOMMEND);
        return switch (order) {
            case CREATE -> improvementComment.createDate.desc();
            case RECOMMEND -> improvementComment.recommendCount.desc();
            case COMMENT -> null;
        };
    }

    private BooleanExpression isImprovementEqualTo(Long improvementId) {
        return improvementComment.improvement.id.eq(improvementId);
    }
}
