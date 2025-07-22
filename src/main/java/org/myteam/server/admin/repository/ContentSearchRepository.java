package org.myteam.server.admin.repository;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.QContentCountCte;
import org.myteam.server.admin.dto.QContentCte;
import org.myteam.server.admin.dto.QMemberReportCte;
import org.myteam.server.admin.entity.AdminChangeLog;
import org.myteam.server.admin.entity.AdminMemo;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.myteam.server.admin.dto.RequestContentDto.*;
import static org.myteam.server.admin.dto.ResponseContentDto.*;
import static org.myteam.server.admin.entity.QAdminMemo.adminMemo;
import static org.myteam.server.admin.utill.AdminControlType.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.board.domain.QBoardCount.boardCount;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.report.domain.QReport.report;

@Transactional
@RequiredArgsConstructor
@Repository
public class ContentSearchRepository {

    private final JPAQueryFactory queryFactory;
    private final BlazeJPAQueryFactory blazeJPAQueryFactory;
    private final SecurityReadService securityReadService;
    private final AdminMemoRepository adminMemoRepository;
    private final AdminChangeLogRepo adminChangeLogRepo;


    public void addAdminMemo(AdminMemoRequest adminMemoRequest) {
        Member admin = securityReadService.getMember();
        if (adminMemoRequest.getContent() != null) {
            AdminMemo adminMemo1 = AdminMemo
                    .builder()
                    .content(adminMemoRequest.getContent())
                    .contentId(adminMemoRequest.getContentId())
                    .staticDataType(adminMemoRequest.getStaticDataType())
                    .writer(admin)
                    .build();
            adminMemoRepository.save(adminMemo1);
        }
        if (adminMemoRequest.getStaticDataType().name().equals(StaticDataType.COMMENT.name())) {
            Comment comment = queryFactory.select(comment1)
                    .from(comment1)
                    .where(comment1.id.eq(adminMemoRequest.getContentId()))
                    .fetchOne();
            if (!comment.getAdminControlType().name().equals(adminMemoRequest.getAdminControlType().name())) {
                AdminChangeLog adminChangeLog = AdminChangeLog
                        .builder()
                        .admin(admin)
                        .contentId(adminMemoRequest.getContentId())
                        .adminControlType(adminMemoRequest.getAdminControlType())
                        .staticDataType(StaticDataType.COMMENT)
                        .build();
                adminChangeLogRepo.save(adminChangeLog);
            }
        }
        if (adminMemoRequest.getStaticDataType().name().equals(StaticDataType.BOARD.name())) {
            Board board1 = queryFactory.select(board)
                    .from(board)
                    .where(board.id.eq(adminMemoRequest.getContentId()))
                    .fetch().get(0);
            if (!board1.getAdminControlType().name().equals(adminMemoRequest.getAdminControlType().name())) {
                AdminChangeLog adminChangeLog = AdminChangeLog
                        .builder()
                        .admin(admin)
                        .contentId(adminMemoRequest.getContentId())
                        .adminControlType(adminMemoRequest.getAdminControlType())
                        .staticDataType(StaticDataType.BOARD)
                        .build();
                adminChangeLogRepo.save(adminChangeLog);
            }
        }
    }

    public Page<ResponseReportList> getReportList(RequestReportList requestReportList) {

        Pageable pageable = PageRequest.of(requestReportList.getOffset(), 10);
        if (requestReportList.getStaticDataType().name().equals(StaticDataType.COMMENT.name())) {
            List<ResponseReportList> responseReportLists = queryFactory
                    .select(
                            Projections.constructor(ResponseReportList.class,
                                    report.reporter.nickname,
                                    new CaseBuilder()
                                            .when(report.reason.eq(BanReason.HARASSMENT))
                                            .then("욕설")
                                            .when(report.reason.eq(BanReason.SEXUAL_CONTENT))
                                            .then("풍기위반")
                                            .when(report.reason.eq(BanReason.POLITICAL_CONTENT))
                                            .then("정치")
                                            .when(report.reason.eq(BanReason.PROMOTIONAL_OR_ILLEGAL_ADS))
                                            .then("광고")
                                            .otherwise("기타"),
                                    report.reportDescription,
                                    report.createDate.stringValue()
                            ))
                    .from(report)
                    .join(comment1)
                    .on(comment1.id.eq(report.reportedContentId).and(report.reportType.eq(ReportType.COMMENT)))
                    .where(report.reportedContentId.eq(requestReportList.getContentId()))
                    .orderBy(report.createDate.desc())
                    .limit(10)
                    .offset(requestReportList.getOffset())
                    .fetch();

            Long totNum = Optional.ofNullable(queryFactory
                            .select(report.count())
                            .from(report)
                            .where(report.reportedContentId.eq(requestReportList.getContentId())
                                    , report.reportType.eq(ReportType.COMMENT))
                            .fetchOne())
                    .orElse(0L);

            responseReportLists.stream()
                    .forEach(x -> {
                        String dateTime = DateFormatUtil
                                .formatByDotAndSlash.format(LocalDateTime.parse(x.getCreateDate()));
                        x.updateCreateDate(dateTime);
                    });

            return new PageImpl<>(responseReportLists, pageable, totNum);
        }
        List<ResponseReportList> responseReportLists = queryFactory
                .select(
                        Projections.constructor(ResponseReportList.class,
                                report.reporter.nickname,
                                new CaseBuilder()
                                        .when(report.reason.eq(BanReason.HARASSMENT))
                                        .then("욕설")
                                        .when(report.reason.eq(BanReason.SEXUAL_CONTENT))
                                        .then("풍기위반")
                                        .when(report.reason.eq(BanReason.POLITICAL_CONTENT))
                                        .then("정치")
                                        .when(report.reason.eq(BanReason.PROMOTIONAL_OR_ILLEGAL_ADS))
                                        .then("광고")
                                        .otherwise("기타"),
                                report.reportDescription,
                                report.createDate.stringValue()
                        ))
                .from(report)
                .join(board)
                .on(board.id.eq(report.reportedContentId).and(report.reportType.eq(ReportType.BOARD)))
                .where(report.reportedContentId.eq(requestReportList.getContentId()))
                .orderBy(report.createDate.desc())
                .limit(10)
                .offset(requestReportList.getOffset())
                .fetch();
        Long totNum = Optional.ofNullable(queryFactory
                .select(report.count())
                .from(report)
                .where(report.reportedContentId.eq(requestReportList.getContentId())
                        , report.reportType.eq(ReportType.BOARD))
                .fetchOne()
        ).orElse(0L);

        responseReportLists.stream()
                .forEach(x -> {
                    String dateTime = DateFormatUtil
                            .formatByDotAndSlash.format(LocalDateTime.parse(x.getCreateDate()));
                    x.updateCreateDate(dateTime);
                });
        return new PageImpl<>(responseReportLists, pageable, totNum);
    }

    public ResponseDetail getDetail(RequestDetail requestDetail) {
        if (requestDetail.getStaticDataType().name().equals(StaticDataType.COMMENT.name())) {
            ResponseDetail responseDetail = queryFactory.select(Projections.constructor(ResponseDetail.class,
                            new CaseBuilder()
                                    .when(comment1.adminControlType.eq(SHOW))
                                    .then("노출")
                                    .when(comment1.adminControlType.eq(PENDING))
                                    .then("보류")
                                    .otherwise("숨김"),
                            JPAExpressions.select(report.id.count())
                                    .from(report)
                                    .where(report.reportedContentId.eq(comment1.id).and(report.reportType.eq(ReportType.COMMENT))),
                            comment1.recommendCount,
                            comment1.createDate.stringValue(),
                            new CaseBuilder()
                                    .when(member.status.eq(MemberStatus.ACTIVE))
                                    .then("정상")
                                    .when(member.status.eq(MemberStatus.INACTIVE))
                                    .then("정지")
                                    .otherwise("경고"),
                            member.nickname,
                            Expressions.constant(""),
                            Expressions.constant(""),
                            comment1.comment,
                            comment1.createdIp
                    ))
                    .from(comment1)
                    .join(member)
                    .on(member.eq(comment1.member))
                    .where(comment1.id.eq(requestDetail.getContentId()))
                    .fetch()
                    .get(0);
            return editDataTime(StaticDataType.BOARD, responseDetail, requestDetail);
        }

        ResponseDetail responseDetail = queryFactory.select(Projections.constructor(ResponseDetail.class,
                        new CaseBuilder()
                                .when(board.adminControlType.eq(SHOW))
                                .then("노출")
                                .when(board.adminControlType.eq(PENDING))
                                .then("보류")
                                .otherwise("숨김"),
                        JPAExpressions.select(report.id.count())
                                .from(report)
                                .where(report.reportedContentId.eq(board.id).and(report.reportType.eq(ReportType.BOARD))),
                        JPAExpressions.select(boardCount.recommendCount)
                                .from(boardCount)
                                .where(boardCount.board.eq(board)),
                        board.createDate.stringValue(),
                        new CaseBuilder()
                                .when(member.status.eq(MemberStatus.ACTIVE))
                                .then("정상")
                                .when(member.status.eq(MemberStatus.INACTIVE))
                                .then("정지")
                                .otherwise("경고"),
                        member.nickname,
                        board.title,
                        board.link,
                        board.content,
                        board.createdIp
                ))
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .where(board.id.eq(requestDetail.getContentId()))
                .fetch()
                .get(0);

        return editDataTime(StaticDataType.BOARD, responseDetail, requestDetail);
    }

    public Page<ResponseContentSearch> getDataList(RequestContentData adminContentResearch) {
        if (adminContentResearch.getStaticDataType() == null) {
            return getWhenDataTypeIsNullWithUnion(adminContentResearch);
        }
        if (adminContentResearch.getStaticDataType().name().equals(StaticDataType.COMMENT.name())) {
            return getAdminCommentList(adminContentResearch);
        }
        return getAdminBoardList(adminContentResearch);
    }

    public Page<ResponseContentSearch> getWhenDataTypeIsNullWithUnion(RequestContentData adminContentResearch) {

        BoardSearchType boardSearchType = adminContentResearch.getBoardSearchType();
        String searchKeyWord = adminContentResearch.getSearchKeyWord();
        LocalDateTime startTime = adminContentResearch.provideStartTime();
        LocalDateTime endTime = adminContentResearch.provideEndTime();

        Pageable pageable = PageRequest.of(adminContentResearch.getOffset(), 10);
        QMemberReportCte reportCte = new QMemberReportCte("reportCte");
        QContentCte cte = new QContentCte("cte");


        //group by문에는 seelct 혹은 bind시 통계데이터만 들어가야되고 상수값이 들ㅇ거ㅏ면안된다. expression.const같은거
        //상수는 group by를 한 값만 그래도 expresssion.const 는 안된다.
        BlazeJPAQuery reportCteQuery = blazeJPAQueryFactory
                .from(report)
                .where(report.reportType.in(ReportType.BOARD, ReportType.COMMENT))
                .groupBy(report.reportedContentId, report.reportType)
                .bind(reportCte.reportedId, report.reportedContentId)
                .bind(reportCte.reportType, report.reportType);

        BlazeJPAQuery blazeJPAQueryWithBoard = blazeJPAQueryFactory
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .where(contentSearchTypeCond(StaticDataType.BOARD, boardSearchType, searchKeyWord),
                        searchByTimeLine(startTime, endTime, StaticDataType.BOARD),
                        processStatusCond(adminContentResearch.getAdminControlType(), StaticDataType.BOARD))
                .bind(cte.contentId, board.id)
                .bind(cte.name, member.nickname)
                .bind(cte.staticDataType, board.staticDataType)
                .bind(cte.content, board.title)
                .bind(cte.createAt, board.createDate)
                .bind(cte.memberStatus, member.status)
                .bind(cte.adminControlType, board.adminControlType);

        //balze api의 문제 인지는 모르겠는대 bind시 expression.constant보단 해당 객체의 프로퍼티
        //        //값으로 가져와서 넣는게 안정적이다. 즉 commtent1.staticDatatType이렇게 넣는게 훨좋다.
        //        //Expression.constant(StatciDataType.xx)이런것보단
        //        //또한 cte에 enum타입이 들어간다면 enumberdate를 string으로 꼭설정하도록하자.
        BlazeJPAQuery blazeJPAQueryWithComment = blazeJPAQueryFactory
                .from(comment1)
                .join(member)
                .on(member.eq(comment1.member))
                .where(contentSearchTypeCond(StaticDataType.COMMENT, boardSearchType, searchKeyWord)
                        , processStatusCond(adminContentResearch.getAdminControlType(), StaticDataType.COMMENT)
                        , searchByTimeLine(startTime, endTime, StaticDataType.COMMENT))
                .bind(cte.contentId, comment1.id)
                .bind(cte.name, member.nickname)
                .bind(cte.staticDataType, comment1.staticDataType)
                .bind(cte.content, comment1.comment)
                .bind(cte.createAt, comment1.createDate)
                .bind(cte.memberStatus, member.status)
                .bind(cte.adminControlType, comment1.adminControlType);

        List<ResponseContentSearch> responseLatestDataList =
                blazeJPAQueryFactory
                        .with(reportCte, reportCteQuery)
                        .with(cte, blazeJPAQueryFactory
                                .unionAll(blazeJPAQueryWithBoard, blazeJPAQueryWithComment))
                        .select(Projections.constructor(
                                ResponseContentSearch.class,
                                cte.contentId,
                                cte.name,
                                new CaseBuilder()
                                        .when(cte.staticDataType.eq(StaticDataType.BOARD))
                                        .then("게시글")
                                        .when(cte.staticDataType.eq(StaticDataType.COMMENT))
                                        .then("댓글")
                                        .otherwise("기타"),
                                cte.content,
                                cte.createAt.stringValue(),
                                new CaseBuilder()
                                        .when(cte.memberStatus.eq(MemberStatus.INACTIVE))
                                        .then("정지")
                                        .when(cte.memberStatus.eq(MemberStatus.ACTIVE))
                                        .then("정상")
                                        .otherwise("경고"),
                                new CaseBuilder()
                                        .when(cte.adminControlType.eq(SHOW))
                                        .then("노출")
                                        .when(cte.adminControlType.eq(HIDDEN))
                                        .then("숨김")
                                        .otherwise("보류"),
                                Expressions.constant(0L),
                                Expressions.constant("미신고")
                        ))
                        .from(cte)
                        .leftJoin(reportCte)
                        .on(reportCte.reportedId.eq(cte.contentId), reportCte.reportType.stringValue()
                                .eq(cte.staticDataType.stringValue()))
                        .where(totReportCond(adminContentResearch.getIsReported(), reportCte))
                        .orderBy(cte.createAt.desc())
                        .limit(10)
                        .offset(0)
                        .fetch();
        updateDetailReportInfo(adminContentResearch.getIsReported(), responseLatestDataList);

        Long totCount = Optional.ofNullable(blazeJPAQueryFactory
                .with(reportCte, reportCteQuery)
                .with(cte, blazeJPAQueryFactory
                        .unionAll(blazeJPAQueryWithBoard, blazeJPAQueryWithComment))
                .select(cte.count())
                .from(cte)
                .leftJoin(reportCte)
                .on(reportCte.reportedId.eq(cte.contentId), reportCte.reportType.stringValue()
                        .eq(cte.staticDataType.stringValue()))
                .where(totReportCond(adminContentResearch.getIsReported(), reportCte))
                .fetchOne()).orElse(0L);

        responseLatestDataList.stream()
                .forEach(x -> {
                    String date = DateFormatUtil.formatByDotAndSlash
                            .format(LocalDateTime.parse(x.getCreateDate()));
                    x.updateCreateDate(date);
                });

        return new PageImpl<>(responseLatestDataList, pageable, totCount);
    }

    public Page<ResponseContentSearch> getAdminCommentList(RequestContentData adminContentResearch) {

        BoardSearchType boardSearchType = adminContentResearch.getBoardSearchType();
        String searchKeyWord = adminContentResearch.getSearchKeyWord();
        LocalDateTime startTime = adminContentResearch.provideStartTime();
        LocalDateTime endTime = adminContentResearch.provideEndTime();
        QContentCountCte cte = new QContentCountCte("cte");

        BlazeJPAQuery countCteQuery = blazeJPAQueryFactory
                .from(report)
                .where(report.reportType.eq(ReportType.COMMENT))
                .groupBy(report.reportedContentId)
                .bind(cte.contentId, report.reportedContentId);

        Pageable pageable = PageRequest.of(adminContentResearch.getOffset(), 10);
        Long totNum = Optional.ofNullable(blazeJPAQueryFactory
                        .with(cte, countCteQuery)
                        .select(comment1.count())
                        .from(comment1)
                        .leftJoin(cte)
                        .on(cte.contentId.eq(comment1.id))
                        .where(contentSearchTypeCond(adminContentResearch.getStaticDataType(), boardSearchType, searchKeyWord)
                                , processStatusCond(adminContentResearch.getAdminControlType(), adminContentResearch.getStaticDataType())
                                , searchByTimeLine(startTime, endTime, adminContentResearch.getStaticDataType())
                                , totReportCond(adminContentResearch.getIsReported()
                                        , cte))
                        .fetchOne())
                .orElse(0L);

        List<ResponseContentSearch> responseContents = blazeJPAQueryFactory
                .with(cte, countCteQuery)
                .select(Projections.constructor(ResponseContentSearch.class,
                        comment1.id,
                        member.nickname,
                        Expressions.constant("댓글"),
                        comment1.comment.substring(0, 20),
                        comment1.createDate.stringValue(),
                        new CaseBuilder()
                                .when(member.status.eq(MemberStatus.ACTIVE))
                                .then("정상")
                                .when(member.status.eq(MemberStatus.INACTIVE))
                                .then("정지")
                                .otherwise("경고"),
                        new CaseBuilder()
                                .when(comment1.adminControlType.eq(SHOW))
                                .then("노출")
                                .when(comment1.adminControlType.eq(PENDING))
                                .then("보류")
                                .otherwise("숨김"),
                        Expressions.constant(0L)
                        , Expressions.constant("미신고")
                ))
                .from(comment1)
                .join(member)
                .on(member.eq(comment1.member))
                .leftJoin(cte)
                .on(cte.contentId.eq(comment1.id))
                .where(contentSearchTypeCond(adminContentResearch.getStaticDataType(), boardSearchType, searchKeyWord)
                        , processStatusCond(adminContentResearch.getAdminControlType(), adminContentResearch.getStaticDataType())
                        , searchByTimeLine(startTime, endTime, adminContentResearch.getStaticDataType()),
                        totReportCond(adminContentResearch.getIsReported(),
                                cte))
                .orderBy(comment1.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        updateDetailReportInfo(adminContentResearch.getIsReported(), ReportType.COMMENT, responseContents);
        return new PageImpl<>(responseContents, pageable, totNum);
    }

    public Page<ResponseContentSearch> getAdminBoardList(RequestContentData adminContentResearch) {
        BoardSearchType boardSearchType = adminContentResearch.getBoardSearchType();
        String searchKeyWord = adminContentResearch.getSearchKeyWord();
        LocalDateTime startTime = adminContentResearch.provideStartTime();
        LocalDateTime endTime = adminContentResearch.provideEndTime();

        QContentCountCte cte = new QContentCountCte("cte");

        BlazeJPAQuery countCteQuery = blazeJPAQueryFactory
                .from(report)
                .where(report.reportType.eq(ReportType.BOARD))
                .groupBy(report.reportedContentId)
                .bind(cte.contentId, report.reportedContentId);

        Pageable pageable = PageRequest.of(adminContentResearch.getOffset(), 10);

        Long totNum = Optional.ofNullable(blazeJPAQueryFactory
                        .with(cte, countCteQuery)
                        .select(board.count())
                        .from(board)
                        .leftJoin(cte)
                        .on(cte.contentId.eq(board.id))
                        .where(contentSearchTypeCond(adminContentResearch.getStaticDataType(),
                                        boardSearchType, searchKeyWord)
                                , processStatusCond(adminContentResearch.getAdminControlType(),
                                        adminContentResearch.getStaticDataType())
                                , searchByTimeLine(startTime, endTime, adminContentResearch.getStaticDataType()),
                                totReportCond(adminContentResearch.getIsReported(), cte))
                        .fetchOne())
                .orElse(0L);

        List<ResponseContentSearch> responseContents = blazeJPAQueryFactory
                .with(cte, countCteQuery)
                .select(Projections.constructor(ResponseContentSearch.class,
                        board.id,
                        member.nickname,
                        Expressions.constant("게시글"),
                        board.title,
                        board.createDate.stringValue(),
                        new CaseBuilder()
                                .when(member.status.eq(MemberStatus.ACTIVE))
                                .then("정상")
                                .when(member.status.eq(MemberStatus.INACTIVE))
                                .then("정지")
                                .otherwise("경고"),
                        new CaseBuilder()
                                .when(board.adminControlType.eq(SHOW))
                                .then("노출")
                                .when(board.adminControlType.eq(PENDING))
                                .then("보류")
                                .otherwise("숨김"),
                        Expressions.constant(0L)
                        , Expressions.constant("미신고")
                ))
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .leftJoin(cte)
                .on(cte.contentId.eq(board.id))
                .where(contentSearchTypeCond(adminContentResearch.getStaticDataType(),
                                boardSearchType, searchKeyWord)
                        , processStatusCond(adminContentResearch.getAdminControlType(),
                                adminContentResearch.getStaticDataType())
                        , searchByTimeLine(startTime, endTime, adminContentResearch.getStaticDataType()),
                        totReportCond(adminContentResearch.getIsReported(), cte))
                .orderBy(board.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        updateDetailReportInfo(adminContentResearch.getIsReported(), ReportType.BOARD, responseContents);

        return new PageImpl<>(responseContents, pageable, totNum);
    }

    private Predicate contentSearchTypeCond(StaticDataType staticDataType, BoardSearchType boardSearchType, String searchKeyWord) {

        if (boardSearchType == null || searchKeyWord == null) {
            return null;
        }

        if (staticDataType.name().equals(StaticDataType.COMMENT.name())) {
            switch (boardSearchType) {

                case NICKNAME -> {
                    return member.nickname.like("%" + searchKeyWord + "%");
                }
                case COMMENT, TITLE_CONTENT, CONTENT -> {
                    return comment1.comment.like("%" + searchKeyWord + "%");
                }
                default -> {
                    return null;
                }
            }
        }
        switch (boardSearchType) {
            case NICKNAME -> {
                return member.nickname.like("%" + searchKeyWord + "%");
            }
            case TITLE -> {
                return board.title.like("%" + searchKeyWord + "%");
            }
            case TITLE_CONTENT -> {
                return board.title.contains(searchKeyWord).or(board.content.like("%" + searchKeyWord + "%"));
            }
            case CONTENT -> {
                return board.content.like("%" + searchKeyWord + "%");
            }
            default -> {
                return null;
            }
        }

    }

    private Predicate processStatusCond(AdminControlType adminControlType, StaticDataType staticDataType) {

        if (adminControlType == null) {
            return null;
        }
        if (staticDataType.name().equals(StaticDataType.COMMENT.name())) {
            switch (adminControlType) {
                case SHOW -> {
                    return comment1.adminControlType.eq(SHOW);
                }
                case PENDING -> {
                    return comment1.adminControlType.eq(PENDING);
                }
                case HIDDEN -> {
                    return comment1.adminControlType.eq(HIDDEN);
                }
            }
        }
        switch (adminControlType) {
            case SHOW -> {
                return board.adminControlType.eq(SHOW);
            }
            case PENDING -> {
                return board.adminControlType.eq(PENDING);
            }
            case HIDDEN -> {
                return board.adminControlType.eq(HIDDEN);
            }
        }

        return null;

    }

    private Predicate searchByTimeLine(LocalDateTime startTime, LocalDateTime endTime, StaticDataType staticDataType) {
        if (staticDataType.name().equals(StaticDataType.BOARD.name())) {
            if (startTime == null & endTime == null) {

                return null;
            }
            if (startTime != null & endTime == null) {

                return board.createDate.after(startTime);
            }
            if (startTime == null) {

                return board.createDate.before(endTime);
            }

            return board.createDate.between(startTime, endTime);
        }

        if (startTime != null & endTime == null) {

            return comment1.createDate.after(startTime);
        }
        if (endTime != null & startTime == null) {

            return comment1.createDate.before(endTime);
        }

        if (startTime == null & endTime == null) {

            return null;
        }
        return comment1.createDate.between(startTime, endTime);


    }

    private Predicate totReportCond(Boolean reported, QMemberReportCte cte) {
        if (reported == null) {
            return null;
        }
        if (reported) {
            return cte.isNotNull();

        }
        return cte.isNull();
    }

    private Predicate totReportCond(Boolean reported, QContentCountCte cte) {
        if (reported == null) {
            return null;
        }
        if (reported) {
            return cte.isNotNull();
        }
        return cte.isNull();
    }

    private void updateDetailReportInfo(Boolean isReported, ReportType reportType,
                                        List<ResponseContentSearch> responseContents) {
        if (isReported == null) {
            List<Long> contentId = responseContents.stream().map(x -> {
                return x.getContentId();
            }).collect(Collectors.toList());
            List<Long> countSearches = contentId.stream().map(x -> {
                return Optional.ofNullable(queryFactory.select(report.count())
                        .from(report)
                        .where(report.reportedContentId.eq(x), report.reportType.eq(reportType))
                        .fetchOne()).orElse(0L);
            }).collect(Collectors.toList());

            IntStream.range(0, responseContents.size())
                    .forEach(i -> {
                        String dateTime = DateFormatUtil.formatByDot.format(LocalDateTime.parse(
                                responseContents.get(i).getCreateDate()
                        ));
                        responseContents.get(i).updateCreateDate(dateTime);
                        if (countSearches.get(i) == 0) {
                            responseContents.get(i).updateCountReported(0L, "미신고");
                        } else {
                            responseContents.get(i).updateCountReported(countSearches.get(i), "신고");
                        }
                    });
            return;
        }
        if (isReported && !responseContents.isEmpty()) {
            List<Long> boardId = responseContents.stream().map(x -> {
                return x.getContentId();
            }).collect(Collectors.toList());
            List<CountSearch> countSearches = queryFactory.select(
                            Projections.constructor(CountSearch.class,
                                    report.reportedContentId
                                    , Expressions.constant("")
                                    , report.id.count()))
                    .from(report)
                    .where(report.reportedContentId.in(boardId), report.reportType.eq(reportType))
                    .groupBy(report.reportedContentId)
                    .fetch();
            IntStream.range(0, responseContents.size())
                    .forEach(i -> {
                        String dateTime = DateFormatUtil.formatByDot.format(LocalDateTime.parse(
                                responseContents.get(i).getCreateDate()
                        ));
                        responseContents.get(i).updateCreateDate(dateTime);
                        responseContents.get(i).updateCountReported(countSearches.get(i).getReportCount(), "신고");
                    });
            return;
        }
    }

    private void updateDetailReportInfo(Boolean isReported, List<ResponseContentSearch> responseContents) {
        if (isReported == null || !isReported) {
            responseContents.stream().forEach(x -> {
                String dateTime = DateFormatUtil.formatByDot.format(LocalDateTime.parse(
                        x.getCreateDate()
                ));
                x.updateCreateDate(dateTime);
            });
            return;
        }
        if (isReported && responseContents.size() > 0) {
            List<CountSearch> contentId = responseContents.stream().map(x -> {
                return new CountSearch(x.getContentId(), x.getAdminControlType(), 0L);
            }).collect(Collectors.toList());

            List<Long> countSearches = contentId.stream().map(x -> {
                if (x.getContentType().equals("게시판")) {
                    return Optional.ofNullable(queryFactory.select(report.count())
                                    .from(report)
                                    .where(report.reportedContentId.eq(x.getContentId()), report.reportType
                                            .eq(ReportType.BOARD))
                                    .fetchOne())
                            .orElse(0L);
                }
                return Optional.ofNullable(queryFactory.select(report.count())
                                .from(report)
                                .where(report.reportedContentId.eq(x.getContentId()), report.reportType
                                        .eq(ReportType.COMMENT))
                                .fetchOne())
                        .orElse(0L);
            }).collect(Collectors.toList());

            IntStream.range(0, responseContents.size())
                    .forEach(i -> {
                        String dateTime = DateFormatUtil.formatByDot.format(LocalDateTime.parse(
                                responseContents.get(i).getCreateDate()
                        ));
                        responseContents.get(i).updateCreateDate(dateTime);
                        if (countSearches.get(i) == 0) {
                            responseContents.get(i).updateCountReported(0L, "미신고");
                        } else {
                            responseContents.get(i).updateCountReported(countSearches.get(i), "신고");
                        }
                    });
        }
    }

    private ResponseDetail editDataTime(StaticDataType staticDataType, ResponseDetail responseDetail
            , RequestDetail requestDetail) {
        if (responseDetail.getReportCount() > 0) {
            responseDetail.updateReported("신고");
        }
        List<AdminMemoResponse> adminMemoResponses = queryFactory
                .select(Projections.constructor(AdminMemoResponse.class,
                        adminMemo.writer.nickname,
                        adminMemo.createDate.stringValue(),
                        adminMemo.content
                ))
                .from(adminMemo)
                .where(adminMemo.staticDataType.eq(staticDataType)
                        .and(adminMemo.contentId.eq(requestDetail.getContentId())))
                .fetch();

        adminMemoResponses.stream().forEach(x -> {
            String dateTime = DateFormatUtil.formatByDotAndSlash.format(LocalDateTime.parse(x.getCreateDate()));
            x.updateCreateDate(dateTime);
        });

        responseDetail.updateAdminMemoResponses(adminMemoResponses);
        String dateTime = DateFormatUtil
                .formatByDotAndSlash.format(LocalDateTime.parse(responseDetail.getCreateDate()));
        responseDetail.updateCreateDate(dateTime);

        return responseDetail;
    }

}
