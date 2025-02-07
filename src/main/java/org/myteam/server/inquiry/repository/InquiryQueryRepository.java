package org.myteam.server.inquiry.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.inquiry.domain.QInquiry;
import org.myteam.server.inquiry.domain.QInquiryAnswer;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InquiryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<InquiryResponse> getInquiryList(UUID memberPublicId,
                                                InquiryOrderType orderType,
                                                InquirySearchType searchType,
                                                String keyword,
                                                Pageable pageable) {
        QInquiry inquiry = QInquiry.inquiry;
        QInquiryAnswer inquiryAnswer = QInquiryAnswer.inquiryAnswer;

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderType, inquiry, inquiryAnswer);

        // 검색 조건 추가
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(inquiry.member.publicId.eq(memberPublicId));
        if (keyword != null && !keyword.trim().isEmpty() && searchType != null) {
            switch (searchType) {
                case ANSWER -> predicate.and(inquiryAnswer.content.containsIgnoreCase(keyword));
                case CONTENT -> predicate.and(inquiry.content.containsIgnoreCase(keyword));
            }
        }

        // 문의 리스트 조회
        List<InquiryResponse> inquiries = queryFactory
                .select(Projections.constructor(InquiryResponse.class,
                        inquiry.id,
                        inquiry.content,
                        inquiry.member.nickname,
                        inquiry.clientIp,
                        inquiry.createdAt,
                        inquiryAnswer.content,
                        inquiryAnswer.answeredAt
                ))
                .from(inquiry)
                .leftJoin(inquiryAnswer).on(inquiry.id.eq(inquiryAnswer.inquiry.id))
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long total = Optional.ofNullable(queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(inquiry.member.publicId.eq(memberPublicId))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(inquiries, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(InquiryOrderType orderType, QInquiry inquiry, QInquiryAnswer inquiryAnswer) {
        if (orderType == InquiryOrderType.ANSWERED) {
            return inquiryAnswer.answeredAt.desc().nullsLast();
        }
        return inquiry.createdAt.desc();
    }
}
