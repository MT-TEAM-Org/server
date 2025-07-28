package org.myteam.server.admin.repository;


import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.utill.CreateAdminMemo;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.myteam.server.admin.dto.AdminMemoRequestDto.AdminMemoImprovementRequest;
import static org.myteam.server.admin.dto.ImprovementResponseDto.*;
import static org.myteam.server.admin.dto.RequestImprovementDto.*;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.improvement.domain.QImprovementCount.improvementCount;
import static org.myteam.server.member.entity.QMember.member;


@Repository
@RequiredArgsConstructor
@Transactional
public class AdminImprovementSearchRepo {

    private final JPAQueryFactory queryFactory;
    private final CreateAdminMemo createAdminMemo;

    public void createAdminMemo(AdminMemoImprovementRequest adminMemoRequest) {
        createAdminMemo.createImprovementMemo(adminMemoRequest, queryFactory);
    }

    public Page<ResponseImprovement> getImprovementList(RequestImprovementList requestImprovementList) {
        Pageable pageable = PageRequest.of(requestImprovementList.getOffset(), 10);
        List<ResponseImprovement> responseImprovementList = queryFactory
                .select(
                        Projections.constructor(ResponseImprovement.class,
                                improvement.id,
                                member.publicId,
                                new CaseBuilder()
                                        .when(improvement.importantStatus.eq(ImportantStatus.HIGH))
                                        .then("높음")
                                        .when(improvement.importantStatus.eq(ImportantStatus.NORMAL))
                                        .then("중간")
                                        .otherwise("낮음"),
                                new CaseBuilder()
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                        .then("접수")
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                        .then("대기")
                                        .otherwise("완료"),
                                improvementCount.recommendCount,
                                member.nickname,
                                improvement.title,
                                improvement.content.substring(0, 20),
                                improvement.createDate.stringValue()
                        ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .join(improvementCount)
                .on(improvementCount.improvement.eq(improvement))
                .where(searchByTimeLine(requestImprovementList.provideStartTime()
                                , requestImprovementList.provideEndTime())
                        , searchByWriter(requestImprovementList.getNickName())
                        , contentSearchCond(requestImprovementList.getContent()),
                        titleSearchCond(requestImprovementList.getTitle()),
                        processStatusCond(requestImprovementList.getImprovementStatus()),
                        searchByEmail(requestImprovementList.getEmail()),
                        searchByImportantStatus(requestImprovementList.getImportantStatus()))
                .orderBy(improvement.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        responseImprovementList.stream()
                .forEach(x -> {
                    x.updateCreateDate(DateFormatUtil.formatByDot
                            .format(LocalDateTime.parse(x.getCreateDate(), DateFormatUtil
                                    .FLEXIBLE_NANO_FORMATTER)));
                });

        Long count = Optional.ofNullable(queryFactory.select(improvement.count())
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .where(searchByTimeLine(requestImprovementList.provideStartTime(), requestImprovementList.provideEndTime())
                        , searchByWriter(requestImprovementList.getNickName())
                        , contentSearchCond(requestImprovementList.getContent()),
                        titleSearchCond(requestImprovementList.getTitle()),
                        processStatusCond(requestImprovementList.getImprovementStatus()),
                        searchByEmail(requestImprovementList.getEmail()),
                        searchByImportantStatus(requestImprovementList.getImportantStatus()))
                .fetchOne()).orElse(0L);
        return new PageImpl<>(responseImprovementList, pageable, count);
    }

    public ResponseImprovementDetail getImprovementDetail(RequestImprovementDetail requestImprovementDetail) {
        ResponseImprovementDetail responseImprovementDetail = queryFactory
                .select(
                        Projections.constructor(
                                ResponseImprovementDetail.class,
                                member.nickname,
                                improvement.createDate.stringValue(),
                                improvement.createdIp,
                                improvement.title,
                                improvement.content,
                                new CaseBuilder()
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                        .then("접수")
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                        .then("대기")
                                        .otherwise("완료"),
                                new CaseBuilder()
                                        .when(improvement.importantStatus.eq(ImportantStatus.HIGH))
                                        .then("높음")
                                        .when(improvement.importantStatus.eq(ImportantStatus.NORMAL))
                                        .then("중간")
                                        .otherwise("낮음")
                        ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .where(improvement.id.eq(requestImprovementDetail.getContentId()))
                .fetchOne();
        responseImprovementDetail.updateCreateDate(DateFormatUtil.formatByDotAndSlash.format(
                LocalDateTime.parse(responseImprovementDetail.getCreateDate()
                        , DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));

        responseImprovementDetail.updateAdminMemoList(
                createAdminMemo.getAdminMemo(StaticDataType.Improvement,
                        requestImprovementDetail.getContentId(), queryFactory));

        return responseImprovementDetail;
    }

    public Page<ResponseMemberImproveList> getMemberImprovementList(RequestMemberImproveList requestMemberImproveList) {
        Pageable pageable = PageRequest.of(requestMemberImproveList.getOffSet(), 10);
        List<ResponseMemberImproveList> responseMemberImproveLists = queryFactory
                .select(
                        Projections.constructor(ResponseMemberImproveList.class,
                                improvement.id,
                                new CaseBuilder()
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                        .then("접수")
                                        .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                        .then("대기")
                                        .otherwise("완료"),
                                new CaseBuilder()
                                        .when(improvement.importantStatus.eq(ImportantStatus.HIGH))
                                        .then("높음")
                                        .when(improvement.importantStatus.eq(ImportantStatus.NORMAL))
                                        .then("중간")
                                        .otherwise("낮음"),
                                improvementCount.recommendCount,
                                member.nickname,
                                improvement.title,
                                improvement.content.substring(0, 10),
                                improvement.createDate.stringValue()
                        ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .join(improvementCount)
                .on(improvementCount.improvement.eq(improvement))
                .where(member.publicId.eq(requestMemberImproveList.getPublicId()))
                .orderBy(improvement.createDate.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();

        responseMemberImproveLists.stream()
                .forEach(x -> {
                    x.updateCreateDate(DateFormatUtil.formatByDot
                            .format(LocalDateTime.parse(x.getCreateDate()
                                    , DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
                });

        Long totCount = Optional.ofNullable(queryFactory
                .select(improvement.count())
                .from(improvement)
                .join(member)
                .on(improvement.member.eq(member))
                .where(member.publicId.eq(requestMemberImproveList.getPublicId()))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(responseMemberImproveLists, pageable, totCount);
    }


    private Predicate titleSearchCond(String searchKeyWord) {
        if (searchKeyWord == null) {
            return null;
        }
        return improvement.title.like("%" + searchKeyWord + "%");
    }

    private Predicate contentSearchCond(String searchKeyWord) {
        if (searchKeyWord == null) {
            return null;
        }
        return improvement.content.like("%" + searchKeyWord + "%");
    }


    private Predicate processStatusCond(ImprovementStatus improvementStatus) {
        if (improvementStatus.equals(ImprovementStatus.COMPLETED)) {

            return improvement.improvementStatus.eq(ImprovementStatus.COMPLETED);
        }
        if (improvementStatus.equals(ImprovementStatus.PENDING)) {
            return improvement.improvementStatus.eq(ImprovementStatus.PENDING);
        }
        if (improvementStatus.equals(ImprovementStatus.RECEIVED)) {
            return improvement.improvementStatus.eq(ImprovementStatus.RECEIVED);
        }
        return null;

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

    private Predicate searchByImportantStatus(ImportantStatus status) {
        if (status == null) {
            return null;
        }
        if (status.equals(ImportantStatus.LOW)) {

            return improvement.importantStatus.eq(ImportantStatus.LOW);
        }
        if (status.equals(ImportantStatus.NORMAL)) {

            return improvement.importantStatus.eq(ImportantStatus.NORMAL);
        }

        return improvement.importantStatus.eq(ImportantStatus.HIGH);

    }

    private Predicate searchByTimeLine(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime != null & endTime == null) {

            return improvement.createDate.after(startTime);
        }
        if (endTime != null & startTime == null) {

            return improvement.createDate.before(endTime);
        }

        if (startTime == null & endTime == null) {

            return null;
        }
        return improvement.createDate.between(startTime, endTime);

    }
}
