package org.myteam.server.admin.repository;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.ctes.QBoardCountCte;
import org.myteam.server.admin.dto.ctes.QCommentCountCte;
import org.myteam.server.admin.dto.ctes.QMemberReportCte;
import org.myteam.server.admin.dto.ctes.QReportCountCte;
import org.myteam.server.admin.entity.AdminChangeLog;
import org.myteam.server.admin.entity.AdminMemo;
import org.myteam.server.admin.repository.simpleRepo.AdminChangeLogRepo;
import org.myteam.server.admin.repository.simpleRepo.AdminMemoRepository;
import org.myteam.server.admin.utill.CreateAdminMemo;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.QMember;
import org.myteam.server.report.domain.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.myteam.server.admin.dto.request.MemberSearchRequestDto.*;
import static org.myteam.server.admin.dto.response.CommonResponseDto.*;
import static org.myteam.server.admin.dto.response.MemberSearchResponseDto.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.board.domain.QBoardCount.boardCount;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.member.entity.QMemberActivity.memberActivity;
import static org.myteam.server.report.domain.QReport.report;


@Repository
@RequiredArgsConstructor
@Transactional
public class AdminMemberRepository {
    private final AdminChangeLogRepo adminChangeLogRepo;
    private final JPAQueryFactory queryFactory;
    private final BlazeJPAQueryFactory blazeJPAQueryFactory;
    private final CreateAdminMemo createAdminMemo;


    public Page<ResponseMemberSearch> getMemberDataList(RequestMemberSearch requestMemberSearch) {
        Pageable pageable = PageRequest.of(requestMemberSearch.getOffset(), 10);
        QBoardCountCte boardCounting = new QBoardCountCte("boardCounting");
        QCommentCountCte commentCount = new QCommentCountCte("commentCount");
        QReportCountCte reportCount = new QReportCountCte("reportCount");

        BlazeJPAQuery blazeJPAQuery = blazeJPAQueryFactory
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .join(boardCount)
                .on(boardCount.board.eq(board))
                .groupBy(member.publicId)
                .bind(boardCounting.publicId, member.publicId)
                .bind(boardCounting.count, board.id.count().coalesce(0L))
                .bind(boardCounting.recommendCount, boardCount.recommendCount.sum().coalesce(0));

        BlazeJPAQuery blazeJPAQuery1 = blazeJPAQueryFactory
                .from(comment1)
                .join(member)
                .on(member.eq(comment1.member))
                .groupBy(member.publicId)
                .bind(commentCount.publicID, member.publicId)
                .bind(commentCount.count, comment1.id.count().coalesce(0L))
                .bind(commentCount.recommendCount, comment1.recommendCount.sum().coalesce(0));

        BlazeJPAQuery blazeJPAQuery2 = blazeJPAQueryFactory
                .from(report)
                .join(member)
                .on(member.eq(report.reported))
                .groupBy(member.publicId)
                .bind(reportCount.publicId, member.publicId)
                .bind(reportCount.count, report.id.count().coalesce(0L));

        List<ResponseMemberSearch> responseSearchUserLists = blazeJPAQueryFactory
                .with(
                        boardCounting, blazeJPAQuery

                )
                .with(
                        commentCount, blazeJPAQuery1
                )
                .with(reportCount, blazeJPAQuery2
                )
                .select(
                        Projections.constructor(ResponseMemberSearch.class,
                                new CaseBuilder()
                                        .when(member.status.eq(MemberStatus.ACTIVE))
                                        .then("정상")
                                        .when(member.status.eq(MemberStatus.INACTIVE))
                                        .then("정지")
                                        .otherwise("경고"),
                                member.nickname,
                                new CaseBuilder()
                                        .when(boardCounting.isNull())
                                        .then(0L)
                                        .otherwise(boardCounting.count),
                                new CaseBuilder()
                                        .when(commentCount.isNull())
                                        .then(0L)
                                        .otherwise(commentCount.count),
                                new CaseBuilder()
                                        .when(reportCount.isNull())
                                        .then(0L)
                                        .otherwise(reportCount.count),
                                new CaseBuilder()
                                        .when(boardCounting.recommendCount.coalesce(0).gt(0)
                                                .and(commentCount.recommendCount.coalesce(0).gt(0)))
                                        .then(boardCounting.recommendCount.add(commentCount.recommendCount))
                                        .when(boardCounting.recommendCount.coalesce(0).eq(0)
                                                .and(commentCount.recommendCount.coalesce(0).gt(0)))
                                        .then(commentCount.recommendCount)
                                        .when(boardCounting.recommendCount.coalesce(0).gt(0)
                                                .and(commentCount.recommendCount.coalesce(0).eq(0)))
                                        .then(boardCounting.recommendCount)
                                        .otherwise(0),
                                new CaseBuilder()
                                        .when(member.genderType.eq(GenderType.F))
                                        .then("여성")
                                        .when(member.genderType.eq(GenderType.M))
                                        .then("남자")
                                        .otherwise("-"),
                                new CaseBuilder()
                                        .when(member.type.eq(MemberType.DISCORD))
                                        .then("디스코드")
                                        .when(member.type.eq(MemberType.GOOGLE))
                                        .then("구글")
                                        .when(member.type.eq(MemberType.KAKAO))
                                        .then("카카오")
                                        .when(member.type.eq(MemberType.NAVER))
                                        .then("네이버")
                                        .otherwise("일반"),
                                member.email,
                                member.tel,
                                member.createDate.stringValue()
                        )
                )
                .from(member)
                .leftJoin(boardCounting)
                .on(boardCounting.publicId.eq(member.publicId))
                .leftJoin(commentCount)
                .on(commentCount.publicID.eq(member.publicId))
                .leftJoin(reportCount)
                .on(reportCount.publicId.eq(member.publicId))
                .where(userListSearchCond(requestMemberSearch), member.role.notIn(MemberRole.ADMIN))
                .orderBy(member.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        responseSearchUserLists.stream().forEach(
                x -> {
                    x.updateCreateDate(
                            DateFormatUtil.formatByDot
                                    .format(LocalDateTime.parse(x.getCreateDate(), DateFormatUtil
                                            .FLEXIBLE_NANO_FORMATTER)));
                }
        );

        Long userTot = Optional.ofNullable(queryFactory.select(member.count())
                        .from(member)
                        .where(userListSearchCond(requestMemberSearch))
                        .fetchOne())
                .orElse(0L);
        return new PageImpl<>(responseSearchUserLists, pageable, userTot);

    }

    public ResponseMemberDetail getMemberDetail(UUID publicId) {

        QBoardCountCte boardCounting = new QBoardCountCte("boardCounting");
        QCommentCountCte commentCount = new QCommentCountCte("commentCount");
        QMember subQueryImprovementMember = new QMember("subQueryImprovementMember");
        QMember subQueryReportedMember = new QMember("subQueryReportedMember");
        QMember subQueryReporterMember = new QMember("subQueryReporterMember");


        BlazeJPAQuery blazeJPAQuery = blazeJPAQueryFactory
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .join(boardCount)
                .on(boardCount.board.eq(board))
                .where(member.publicId.eq(publicId))
                .groupBy(member.publicId)
                .bind(boardCounting.publicId, member.publicId)
                .bind(boardCounting.count, board.id.count().coalesce(0L))
                .bind(boardCounting.recommendCount, boardCount.recommendCount.sum().coalesce(0));

        BlazeJPAQuery blazeJPAQuery1 = blazeJPAQueryFactory
                .from(comment1)
                .join(member)
                .on(member.eq(comment1.member))
                .where(member.publicId.eq(publicId))
                .groupBy(member.publicId)
                .bind(commentCount.publicID, member.publicId)
                .bind(commentCount.count, comment1.id.count().coalesce(0L))
                .bind(commentCount.recommendCount, comment1.recommendCount.sum().coalesce(0));

        ResponseMemberDetail responseMemberDetail = blazeJPAQueryFactory
                .with(
                        boardCounting, blazeJPAQuery

                )
                .with(
                        commentCount, blazeJPAQuery1
                )
                .select(Projections.constructor(ResponseMemberDetail.class,
                        member.nickname,
                        member.email,
                        member.tel,
                        new CaseBuilder()
                                .when(member.genderType.eq(GenderType.M))
                                .then("남성")
                                .when(member.genderType.eq(GenderType.F))
                                .then("여성")
                                .otherwise("-"),
                        new CaseBuilder()
                                .when(member.type.eq(MemberType.DISCORD))
                                .then("디스코드")
                                .when(member.type.eq(MemberType.GOOGLE))
                                .then("구글")
                                .when(member.type.eq(MemberType.KAKAO))
                                .then("카카오")
                                .when(member.type.eq(MemberType.NAVER))
                                .then("네이버")
                                .otherwise("일반"),
                        member.birthYear,
                        member.birthMonth,
                        member.birthDay,
                        member.createDate.stringValue(),
                        Expressions.constant("hello"),
                        /*memberActivity.latestAccessTime,
                        memberActivity.latestIp,
                        추후에 memberaccess객체를 가져와서 작업해야되는걸로 생각됨.
                        */
                        Expressions.constant("hello"),
                        memberActivity.visitCount,
                        boardCounting.recommendCount.add(commentCount.recommendCount),
                        JPAExpressions.select(improvement.count())
                                .from(improvement)
                                .join(subQueryImprovementMember)
                                .on(subQueryImprovementMember.eq(improvement.member))
                                .where(subQueryImprovementMember.publicId.eq(publicId)),
                        boardCounting.count,
                        commentCount.count,
                        JPAExpressions
                                .select(
                                        report.reported.count()
                                )
                                .from(report)
                                .join(subQueryReportedMember)
                                .on(subQueryReportedMember.eq(report.reported))
                                .where(subQueryReportedMember.publicId.eq(publicId)),
                        JPAExpressions
                                .select(
                                        report.reporter.count()
                                )
                                .from(report)
                                .join(subQueryReporterMember)
                                .on(subQueryReporterMember.eq(report.reporter))
                                .where(subQueryReporterMember.publicId.eq(publicId)),
                        new CaseBuilder()
                                .when(member.status.eq(MemberStatus.ACTIVE))
                                .then("정상")
                                .when(member.status.eq(MemberStatus.INACTIVE))
                                .then("정지")
                                .otherwise("경고")
                ))
                .from(member)
                .join(boardCounting)
                .on(boardCounting.publicId.eq(member.publicId))
                .join(commentCount)
                .on(commentCount.publicID.eq(member.publicId))
                .join(memberActivity)
                .on(memberActivity.member.eq(member))
                .where(member.publicId.eq(publicId))
                .fetch()
                .get(0);

        editTime(publicId, responseMemberDetail);

        return responseMemberDetail;

    }

    public void updateMemberStatus(Member writer, UUID publicId, String content, MemberStatus targetStatus,
                                   MemberStatus memberStatus) {
        createAdminMemo.createMemberMemo(writer,
               publicId,content,targetStatus,memberStatus);
    }


    public Page<ResponseReportList> getMemberReportedList(RequestMemberDetail requestMemberDetail) {

        UUID publicId = requestMemberDetail.getPublicId();
        Pageable pageable = PageRequest.of(requestMemberDetail.getOffset(), 10);
        QMemberReportCte cte = new QMemberReportCte("cte");
        List<ResponseReportList> responseReportLists = blazeJPAQueryFactory
                .with(cte, blazeJPAQueryFactory
                        .from(report)
                        .groupBy(report.reportedContentId, report.reportType)
                        .bind(cte.reportedId, report.reportedContentId)
                        .bind(cte.reportType, report.reportType)
                        .bind(cte.reportCount, report.count())
                )
                .select(
                        Projections.constructor(ResponseReportList.class,
                                new CaseBuilder()
                                        .when(board.isNotNull())
                                        .then(board.id)
                                        .otherwise(comment1.id),
                                report.createDate.stringValue(),
                                cte.reportCount,
                                new CaseBuilder()
                                        .when(report.reportType.eq(ReportType.COMMENT))
                                        .then("댓글")
                                        .when(report.reportType.eq(ReportType.BOARD))
                                        .then("게시글")
                                        .otherwise("기타"),
                                new CaseBuilder()
                                        .when(board.isNotNull())
                                        .then(board.title)
                                        .when(comment1.isNotNull())
                                        .then(comment1.comment.substring(0, 20))
                                        .otherwise("기타"))
                )
                .from(report)
                .join(member)
                .on(report.reported.eq(member))
                .join(cte)
                .on(cte.reportedId.eq(report.reportedContentId)
                        .and(report.reportType.eq(cte.reportType)))
                .leftJoin(board)
                .on(board.id.eq(report.reportedContentId)
                        .and(report.reportType.eq(ReportType.BOARD)))
                .leftJoin(comment1)
                .on(comment1.id.eq(report.reportedContentId)
                        .and(report.reportType.eq(ReportType.COMMENT)))
                .where(report.reported.publicId.eq(publicId))
                .orderBy(report.createDate.desc())
                .limit(10)
                .offset(pageable.getOffset())
                .fetch();


        Long totCount = Optional.ofNullable(queryFactory
                        .select(report.count())
                        .from(report)
                        .join(member)
                        .on(report.reported.publicId.eq(publicId))
                        .fetchOne())
                .orElse(0L);


        responseReportLists.stream().forEach(x -> {
            x.updateReportDate(DateFormatUtil.formatByDot.
                    format(LocalDateTime.
                            parse(x.getReportedDate(), DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
        });

        return new PageImpl<>(responseReportLists, pageable, totCount);

    }


    private BooleanBuilder userListSearchCond(RequestMemberSearch requestMemberSearch) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (requestMemberSearch.getNickName() != null) {

            booleanBuilder.and(member.nickname.like("%" + requestMemberSearch.getNickName() + "%"));
        }
        if (requestMemberSearch.getEmail() != null) {

            booleanBuilder.and(member.email.like("%" + requestMemberSearch.getEmail() + "%"));
        }
        if (requestMemberSearch.getTel() != null) {

            booleanBuilder.and(member.tel.like("%" + requestMemberSearch.getTel() + "%"));
        }
        if (requestMemberSearch.getSignInStart() != null) {

            booleanBuilder.and(member.createDate.goe(requestMemberSearch.provideStartTime()));
        }
        if (requestMemberSearch.getSignInEnd() != null) {

            booleanBuilder.and(member.createDate.loe(requestMemberSearch.provideEndTime()));
        }
        if (requestMemberSearch.getMemberType() != null) {
            booleanBuilder.and(member.type.eq(requestMemberSearch.getMemberType()));
        }
        if (requestMemberSearch.getGenderType() != null) {
            booleanBuilder.and(member.genderType.eq(requestMemberSearch.getGenderType()));
        }

        if (requestMemberSearch.getStatus() != null) {
            booleanBuilder.and(member.status.eq(requestMemberSearch.getStatus()));
        }

        if (requestMemberSearch.getBirthDate() != null) {
            int birthYear = Integer.parseInt(requestMemberSearch.getBirthDate().substring(0, 2));
            int birthMonth = Integer.parseInt(requestMemberSearch.getBirthDate().substring(2, 4));
            int birthDay = Integer.parseInt(requestMemberSearch.getBirthDate().substring(4));
            booleanBuilder.and(member.birthYear.eq(birthYear));
            booleanBuilder.and(member.birthMonth.eq(birthMonth));
            booleanBuilder.and(member.birthDay.eq(birthDay));
        }


        return booleanBuilder;
    }

    private void editTime(UUID publicId
            , ResponseMemberDetail responseMemberDetail) {
        List<AdminMemoResponse> adminMemoResponses=
                createAdminMemo.getMemberAdminMemo(publicId,queryFactory);
        responseMemberDetail.updateAdminMemoResponse(adminMemoResponses);
    }
}
