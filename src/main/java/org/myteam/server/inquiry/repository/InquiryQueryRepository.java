package org.myteam.server.inquiry.repository;

import static org.myteam.server.comment.domain.QInquiryComment.inquiryComment;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.inquiry.domain.QInquiryCount.inquiryCount;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.inquiry.domain.QInquiry;
import org.myteam.server.inquiry.dto.response.InquiryResponse.InquirySaveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InquiryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<InquirySaveResponse> getInquiryList(UUID memberPublicId,
                                                    InquiryOrderType orderType,
                                                    InquirySearchType searchType,
                                                    String keyword,
                                                    Pageable pageable) {
        // 정렬 조건 설정

        // 검색 조건
        log.info("검색조건: {}&&{}", isMemberEqualTo(memberPublicId), getSearchCondition(searchType, keyword));

        // 문의 리스트 조회
        List<InquirySaveResponse> inquiries = queryFactory
                .select(Projections.constructor(InquirySaveResponse.class,
                        inquiry.id,
                        inquiry.content,
                        inquiry.clientIp,
                        inquiry.createdAt,
                        inquiry.member.publicId,
                        inquiry.member.nickname,
                        inquiry.isAdminAnswered.when(true).then("답변완료").otherwise("접수완료"),
                        inquiryCount.commentCount
                ))
                .from(inquiry)
                .join(inquiryCount).on(inquiry.id.eq(inquiryCount.inquiry.id))
                .join(member).on(member.publicId.eq(inquiry.member.publicId))
                .where(
                        isMemberEqualTo(memberPublicId),
                        getSearchCondition(searchType, keyword),
                        getOrderType(orderType)
                )
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long total = getInquiryCount(memberPublicId, searchType, keyword, orderType);

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
                        getSearchCondition(searchType, keyword),
                        getOrderType(orderType)
                )
                .fetchOne()
        ).orElse(0L);
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

    private BooleanExpression getSearchCondition(InquirySearchType searchType, String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }

        return switch (searchType) {
            case CONTENT -> inquiry.content.like("%" + search + "%");
            case COMMENT -> inquiryComment.comment.like("%" + search + "%");
            default -> null;
        };
    }

    private BooleanExpression getOrderType(InquiryOrderType orderType) {
        if (orderType == InquiryOrderType.ANSWERED) {
            return inquiry.isAdminAnswered.isTrue();
        }
        return null;
    }

    public Long findPreviousInquiryI(Long inquiryId) {
        return queryFactory
                .select(inquiry.id)
                .from(inquiry)
                .where(inquiry.id.lt(inquiryId))
                .orderBy(inquiry.id.desc()) // 가장 큰 ID (즉, 이전 글)
                .limit(1)
                .fetchOne();
    }


    public Long findNextInquiryId(Long inquiryId) {
        return queryFactory
                .select(inquiry.id)
                .from(inquiry)
                .where(inquiry.id.gt(inquiryId))
                .orderBy(inquiry.id.asc()) // 가장 작은 ID (즉, 다음 글)
                .limit(1)
                .fetchOne();
    }
}