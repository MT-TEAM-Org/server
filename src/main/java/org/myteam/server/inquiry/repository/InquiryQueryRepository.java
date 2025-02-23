package org.myteam.server.inquiry.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.inquiry.domain.QInquiry;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.inquiry.domain.QInquiry.*;
import static org.myteam.server.inquiry.domain.QInquiryComment.inquiryComment;
import static org.myteam.server.inquiry.domain.QInquiryCount.*;
import static org.myteam.server.member.entity.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InquiryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<InquiryResponse> getInquiryList(UUID memberPublicId,
                                                InquiryOrderType orderType,
                                                InquirySearchType searchType,
                                                String keyword,
                                                Pageable pageable) {
        // 정렬 조건 설정
//        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderType, inquiry);
//        log.info("정렬 조건: {}", orderSpecifier);

        // 검색 조건
        log.info("검색조건: {}&&{}", isMemberEqualTo(memberPublicId), getSearchCondition(searchType, keyword));

        // 문의 리스트 조회
        List<InquiryResponse> inquiries = queryFactory
                .select(Projections.constructor(InquiryResponse.class,
                        inquiry.id,
                        inquiry.content,
                        inquiry.clientIp,
                        inquiry.createdAt,
                        member.publicId,
                        member.nickname,
                        inquiry.isAdminAnswered.when(true).then("답변완료").otherwise("접수완료"),
                        inquiryCount.commentCount
                ))
                .from(inquiry)
                .join(inquiryCount).on(inquiry.id.eq(inquiryCount.inquiry.id))
                .join(member).on(member.publicId.eq(memberPublicId))
                .where(
//                        isMemberEqualTo(memberPublicId),
                        getSearchCondition(searchType, keyword)
//                        getOrderType(orderType)
                )
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long total = getInquiryCount(memberPublicId, searchType, keyword);

        return new PageImpl<>(inquiries, pageable, total);
    }

    public int getMyInquires(UUID memberPublicId) {
        log.info("publicID: {} 문의내역 수 조회", memberPublicId);
        return queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(inquiry.member.publicId.eq(memberPublicId))
                .fetchOne()
                .intValue();
    }

    private long getInquiryCount(UUID memberPublicId,
                                 InquirySearchType searchType,
                                 String keyword) {
        return Optional.ofNullable(queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(
                        isMemberEqualTo(memberPublicId),
                        getSearchCondition(searchType, keyword)
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
}