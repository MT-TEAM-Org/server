package org.myteam.server.admin.repository;


import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.AdminContentMemo;
import org.myteam.server.admin.utill.CreateAdminMemo;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoInquiryRequest;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.*;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryList;
import static org.myteam.server.admin.dto.response.InquiryResponseDto.ResponseInquiryListCond;
import static org.myteam.server.admin.dto.request.InquiryRequestDto.*;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
@Transactional
public class InquirySearchRepo {

    private final JPAQueryFactory queryFactory;
    private final CreateAdminMemo createAdminMemo;

    public AdminContentMemo createAdminMemo(AdminMemoInquiryRequest adminMemoRequest) {

        return createAdminMemo.createInquiryAdminMemo(adminMemoRequest, queryFactory);
    }

    public ResponseInquiryDetail getInquiryDetail(RequestInquiryDetail requestInquiryDetail) {
        ResponseInquiryDetail responseInquiryDetail = queryFactory
                .select(
                        Projections.constructor(ResponseInquiryDetail.class,
                                inquiry.id,
                                new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then("답변완료")
                                        .otherwise("답변대기"),
                                inquiry.createdAt.stringValue(),
                                inquiry.clientIp,
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("비회원")
                                        .otherwise("회원"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("-")
                                        .otherwise(member.nickname),
                                inquiry.email,
                                inquiry.content
                        ))
                .from(inquiry)
                .leftJoin(member)
                .on(member.eq(inquiry.member))
                .where(inquiry.id.eq(requestInquiryDetail.getContentId()))
                .fetchOne();

        responseInquiryDetail.updateCreateDate(DateFormatUtil.formatByDot
                .format(LocalDateTime.parse(responseInquiryDetail.getCreateDate()
                        , DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        responseInquiryDetail.updateAdminMemoList(
                createAdminMemo.getAdminContentMemo(StaticDataType.Inquiry,
                        requestInquiryDetail.getContentId(), queryFactory));

        return responseInquiryDetail;
    }

    public Page<ResponseInquiryList> getInquiryList(RequestInquiryList requestInquiryList) {

        Pageable pageable = PageRequest.of(requestInquiryList.getOffset(), 10);
        List<ResponseInquiryList> inquiryList = queryFactory
                .select(
                        Projections.constructor(ResponseInquiryList.class,
                                inquiry.id,
                                new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then("답변대기")
                                        .otherwise("답변완료"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("비회원")
                                        .otherwise("회원"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("-")
                                        .otherwise(member.nickname),
                                inquiry.email,
                                inquiry.content,
                                inquiry.createdAt.stringValue()
                        ))
                .from(inquiry)
                .leftJoin(member)
                .on(member.eq(inquiry.member))
                .where(inquiry.email.eq(requestInquiryList.getEmail()))
                .orderBy(inquiry.createdAt.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();

        inquiryList.stream()
                .forEach(x -> {
                    x.updateCreateDate(DateFormatUtil.formatByDot
                            .format(LocalDateTime.parse(x.getCreateDate()
                                    , DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
                });

        Long totCount = Optional.ofNullable(queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .leftJoin(member)
                .on(member.eq(inquiry.member))
                .where(inquiry.email.eq(requestInquiryList.getEmail()))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(inquiryList, pageable, totCount);
    }

    public Page<ResponseInquiryListCond> getInquiryListByCond(RequestInquiryListCond requestInquiryListCond) {

        Pageable pageable = PageRequest.of(requestInquiryListCond.getOffset(), 10);

        List<ResponseInquiryListCond> responseInquiryListConds = queryFactory
                .select(Projections.constructor(ResponseInquiryListCond.class,
                                inquiry.id
                                , new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then("답변대기")
                                        .otherwise("답변완료"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then("비회원")
                                        .otherwise("회원"),
                                new CaseBuilder()
                                        .when(member.isNull())
                                        .then(inquiry.email)
                                        .otherwise(member.nickname),
                                inquiry.content,
                                new CaseBuilder()
                                        .when(member.isNotNull())
                                        .then(member.publicId.toString())
                                        .otherwise(""),
                                inquiry.createdAt.stringValue()
                        )
                )
                .from(inquiry)
                .leftJoin(member)
                .on(inquiry.member.eq(member))
                .where(contentSearchCond(requestInquiryListCond.getContent()),
                        searchByWriter(requestInquiryListCond.getNickName()),
                        searchByTimeLine(requestInquiryListCond.provideStartTime()
                                , requestInquiryListCond.provideEndTime()),
                        searchByEmail(requestInquiryListCond.getEmail()),
                        processStatusCond(requestInquiryListCond.getIsAnswered())
                        , memberOrNot(requestInquiryListCond.getIsMember()))
                .orderBy(inquiry.createdAt.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();

        responseInquiryListConds.stream()
                .forEach(x -> {
                    x.updateCreateDate(DateFormatUtil.formatByDot
                            .format(LocalDateTime.parse(x.getCreateDate(), DateFormatUtil
                                    .FLEXIBLE_NANO_FORMATTER)));
                });

        Long count = Optional.ofNullable(queryFactory.select(inquiry.count())
                .from(inquiry)
                .leftJoin(member)
                .on(inquiry.member.eq(member))
                .where(contentSearchCond(requestInquiryListCond.getContent()),
                        searchByWriter(requestInquiryListCond.getNickName()),
                        searchByTimeLine(requestInquiryListCond.provideStartTime(), requestInquiryListCond.provideEndTime()),
                        searchByEmail(requestInquiryListCond.getEmail()),
                        processStatusCond(requestInquiryListCond.getIsAnswered())
                        , memberOrNot(requestInquiryListCond.getIsMember()))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(responseInquiryListConds, pageable, count);
    }


    private Predicate memberOrNot(Boolean isMember) {
        if (isMember == null) {
            return null;
        }
        if (isMember) {
            return inquiry.member.isNotNull();
        }
        return inquiry.member.isNull();
    }

    private Predicate contentSearchCond(String searchKeyWord) {
        if (searchKeyWord == null) {
            return null;
        }
        return inquiry.content.like("%" + searchKeyWord + "%");
    }

    private Predicate processStatusCond(Boolean completed) {
        if (completed == null) {
            return null;
        }
        if (completed) {
            return inquiry.isAdminAnswered.isTrue();
        }
        return inquiry.isAdminAnswered.isFalse();
    }

    private Predicate searchByWriter(String nickName) {
        if (nickName == null) {
            return null;
        }
        return member.nickname.like("%" + nickName + "%");
    }

    private Predicate searchByEmail(String email) {
        if (email == null) {
            return null;
        }
        return member.email.like("%" + email + "%");
    }

    private Predicate searchByTimeLine(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime != null & endTime == null) {

            return inquiry.createdAt.after(startTime);
        }
        if (endTime != null & startTime == null) {

            return inquiry.createdAt.before(endTime);
        }

        if (startTime == null & endTime == null) {

            return null;
        }
        return inquiry.createdAt.between(startTime, endTime);

    }


}
