package org.myteam.server.inquiry.repository;

import static org.myteam.server.admin.dto.AdminDetail.*;
import static org.myteam.server.admin.dto.AdminSearch.*;
import static org.myteam.server.comment.domain.QInquiryComment.inquiryComment;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.inquiry.domain.QInquiryCount.inquiryCount;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.dto.AdminDetail;
import org.myteam.server.admin.dto.AdminSearch;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QInquiryComment;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.inquiry.domain.QInquiry;
import org.myteam.server.inquiry.dto.response.InquiryResponse.InquiryDto;
import org.myteam.server.report.domain.DomainType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InquiryQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final RedisCountService redisCountService;

    public Page<InquiryDto> getInquiryList(UUID memberPublicId,
                                           InquiryOrderType orderType,
                                           InquirySearchType searchType,
                                           String keyword,
                                           Pageable pageable) {
        // 정렬 조건 설정

        // 검색 조건
        log.info("검색조건: {}&&{}", isMemberEqualTo(memberPublicId), isSearchTypeLikeTo(searchType, keyword));

        // 문의 리스트 조회
        List<InquiryDto> inquiries = queryFactory
                .select(Projections.fields(InquiryDto.class,
                        inquiry.id,
                        inquiry.content,
                        inquiry.clientIp,
                        inquiry.createdAt,
                        inquiry.member.publicId,
                        inquiry.member.nickname,
                        inquiry.isAdminAnswered.when(true).then("답변완료").otherwise("접수완료").as("isAdminAnswered"),
                        inquiryCount.commentCount
                ))
                .from(inquiry)
                .join(inquiryCount).on(inquiry.id.eq(inquiryCount.inquiry.id))
                .join(member).on(member.publicId.eq(inquiry.member.publicId))
                .where(
                        isSearchTypeLikeTo(searchType, keyword),
                        isMemberEqualTo(memberPublicId),
                        getOrderType(orderType)
                )
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (InquiryDto inquiryDto : inquiries) {
            Long id = inquiryDto.getId();
            CommonCountDto commonCount = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.INQUIRY, id,
                    null);
            inquiryDto.setCommentCount(commonCount.getCommentCount());
        }

        // 전체 개수 조회
        long total = getInquiryCount(memberPublicId, searchType, keyword, orderType);

        if (searchType == InquirySearchType.COMMENT) {
            inquiries.forEach(inquiry -> {
                CommentSearchDto commentSearch = getSearchInquiryComment(inquiry.getId(), keyword);
                inquiry.setCommentSearchList(commentSearch);
            });
        }

        return new PageImpl<>(inquiries, pageable, total);
    }

    public int getMyInquires(UUID memberPublicId) {
        log.info("publicID: {} 문의내역 수 조회", memberPublicId);
        return queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(isMemberEqualTo(memberPublicId))
                .fetchOne()
                .intValue();
    }

    private long getInquiryCount(UUID memberPublicId,
                                 InquirySearchType searchType,
                                 String keyword,
                                 InquiryOrderType orderType) {
        return Optional.ofNullable(queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .join(inquiryCount).on(inquiry.id.eq(inquiryCount.inquiry.id))
                .join(member).on(member.publicId.eq(inquiry.member.publicId))
                .leftJoin(inquiryComment).on(inquiry.id.eq(inquiryComment.inquiry.id))
                .where(
                        isMemberEqualTo(memberPublicId),
                        isSearchTypeLikeTo(searchType, keyword),
                        getOrderType(orderType)
                )
                .fetchOne()
        ).orElse(0L);
    }







    private CommentSearchDto getSearchInquiryComment(Long inquiryId, String search) {
        JPQLQuery<CommentSearchDto> query = queryFactory
                .select(Projections.fields(CommentSearchDto.class,
                        inquiryComment.id.as("commentId"),
                        inquiryComment.comment,
                        inquiryComment.imageUrl
                ))
                .from(inquiryComment)
                .where(
                        inquiryComment.inquiry.id.eq(inquiryId),
                        inquiryComment.comment.like("%" + search + "%")
                );

        return query.orderBy(
                inquiryComment.createDate.desc(),
                inquiryComment.comment.asc()
        ).fetchFirst();
    }

    private BooleanExpression isSearchTypeLikeTo(InquirySearchType searchType, String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }

        return switch (searchType) {
            case CONTENT -> inquiry.content.like("%" + search + "%");
            case COMMENT -> JPAExpressions.selectOne()
                    .from(QComment.comment1)
                    .where(
                            QComment.comment1.comment.like("%" + search + "%")
                                    .and(QComment.comment1.commentType.eq(CommentType.INQUIRY))
                                    .and(QComment.comment1.as(QInquiryComment.class).inquiry.id.eq(
                                            inquiry.id))
                    )
                    .exists();
            default -> null;
        };
    }

    private OrderSpecifier<?> getOrderSpecifier(InquiryOrderType orderType, QInquiry inquiry) {
        if (orderType == InquiryOrderType.ANSWERED) {
            return inquiry.isAdminAnswered.desc();
        }
        return inquiry.createdAt.desc();
    }

    private BooleanExpression isMemberEqualTo(UUID memberPublicId) {
        return memberPublicId != null ? inquiry.member.publicId.eq(memberPublicId) : null;
    }

    private BooleanExpression getOrderType(InquiryOrderType orderType) {
        if (orderType == InquiryOrderType.ANSWERED) {
            return inquiry.isAdminAnswered.isTrue();
        }
        return null;
    }

    public Long findPreviousInquiry(Long inquiryId, UUID memberPublicId) {
        return queryFactory
                .select(inquiry.id)
                .from(inquiry)
                .where(
                        inquiry.id.lt(inquiryId),
                        isMemberEqualTo(memberPublicId)
                )
                .orderBy(inquiry.id.desc()) // 가장 큰 ID (즉, 이전 글)
                .limit(1)
                .fetchOne();
    }


    public Long findNextInquiryId(Long inquiryId, UUID memberPublicId) {
        return queryFactory
                .select(inquiry.id)
                .from(inquiry)
                .where(
                        inquiry.id.gt(inquiryId),
                        isMemberEqualTo(memberPublicId)
                )
                .orderBy(inquiry.id.asc()) // 가장 작은 ID (즉, 다음 글)
                .limit(1)
                .fetchOne();
    }
}