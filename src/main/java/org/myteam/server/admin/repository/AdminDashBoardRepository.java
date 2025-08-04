package org.myteam.server.admin.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.utill.*;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.ReportType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.member.entity.QMemberAccess.memberAccess;
import static org.myteam.server.report.domain.QReport.report;

@Repository
@RequiredArgsConstructor
@Transactional
public class AdminDashBoardRepository {

    private final JPAQueryFactory queryFactory;
    private final RedisService redisService;
    private final SecurityReadService securityReadService;


    public List<ResponseStatic> getStaticData(StaticDataType staticDataType, DateType dateType) {

        return getStaticDataByRequest(dateType, staticDataType);
    }

    private List<ResponseStatic> getStaticDataByRequest(DateType dateType, StaticDataType staticDataType) {
        LocalDateTime now = LocalDateTime.now();

        List<LocalDateTime> dateList = DateTypeFactory.SupplyDateTime(dateType, now);

        if(staticDataType.name().equals(StaticDataType.DashBoard.name())){
            List<ResponseStatic> responseStatics=new ArrayList<>();
            responseStatics.add(CreateStaticQueryFactory.createStaticQuery(board, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createStaticQuery(comment1, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createReportStaticQuery(dateType, dateList, queryFactory,ReportType.BOARD));
            responseStatics.add(CreateStaticQueryFactory.createReportStaticQuery(dateType, dateList, queryFactory, ReportType.COMMENT));
            responseStatics.add(makeInquiryImprovementStatic(dateType,dateList));
            responseStatics.add(CreateStaticQueryFactory.createStaticQuery(memberAccess, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createUserDelStatic(dateType,dateList,queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createStaticQuery(member, dateType, dateList, queryFactory));
            return responseStatics;
        }
        if(staticDataType.name().equals(StaticDataType.MemberBoard.name())){
            List<ResponseStatic> responseStatics=new ArrayList<>();
            responseStatics.add(CreateStaticQueryFactory.createSimpleStaticQuery(member, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createSimpleStaticQuery(memberAccess, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createSimpleUserDelStatic(dateType,dateList,queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createStaticMemberStatusQuery(
                    MemberStatus.WARNED,dateList,queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createStaticMemberStatusQuery(
                    MemberStatus.INACTIVE,dateList,queryFactory));
            return responseStatics;
        }
        if(staticDataType.name().equals(StaticDataType.ContentBoard.name())){
            List<ResponseStatic> responseStatics=new ArrayList<>();
            responseStatics.add(CreateStaticQueryFactory.createSimpleStaticQuery(board, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createSimpleStaticQuery(comment1, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createSimpleReportStaticQuery(dateType, dateList, queryFactory,ReportType.BOARD));
            responseStatics.add(CreateStaticQueryFactory.createSimpleReportStaticQuery(dateType, dateList, queryFactory, ReportType.COMMENT));
            responseStatics.add(CreateStaticQueryFactory.createStaticContentQuery(StaticDataType.BOARD,AdminControlType.HIDDEN
                    ,dateList,queryFactory));
            responseStatics.add(CreateStaticQueryFactory.createStaticContentQuery(StaticDataType.COMMENT,AdminControlType.HIDDEN
                    ,dateList,queryFactory));
            return responseStatics;
        }
        if(staticDataType.name().equals(StaticDataType.Inquiry.name())){
            List<ResponseStatic> responseStatics=new ArrayList<>();
            responseStatics.add(CreateStaticQueryFactory.createSimpleStaticQuery(
                    inquiry, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                    .createInquiryStaticQuery(dateList,false,queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                    .createInquiryStaticQuery(dateList,true,queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                    .createMemberInquiryStaticQuery(dateList,true,queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                .createMemberInquiryStaticQuery(dateList,false,queryFactory));
            return responseStatics;
        }
        if(staticDataType.name().equals(StaticDataType.Improvement.name())){
            List<ResponseStatic> responseStatics=new ArrayList<>();
            responseStatics.add(CreateStaticQueryFactory.createSimpleStaticQuery(
                    improvement, dateType, dateList, queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                    .createImprovementStaticQuery(dateList,StaticDataType.ImprovementPending,queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                    .createImprovementStaticQuery(dateList,StaticDataType.ImprovementReceived,queryFactory));
            responseStatics.add(CreateStaticQueryFactory
                    .createImprovementStaticQuery(dateList,StaticDataType.ImprovementComplete,queryFactory));
            return responseStatics;

        }
        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER, "없는 형식의 파라미터 입니다");
    }



    public Map<String,List<ResponseLatestData>> getLatestData() {
        Member admin = securityReadService.getMember();
        Map<String,List<ResponseLatestData>> latestDataResult=new HashMap<>();
        List<ResponseLatestData> reportLatest=queryFactory.select(
                            Projections.constructor(ResponseLatestData.class,
                                    new CaseBuilder()
                                            .when(report.reportType.eq(ReportType.COMMENT))
                                            .then("댓글")
                                            .when(report.reportType.eq(ReportType.BOARD))
                                            .then("게시글")
                                            .when(report.reportType.eq(ReportType.NEWS))
                                            .then("뉴스")
                                            .when(report.reportType.eq(ReportType.CHAT))
                                            .then("채팅")
                                            .otherwise("기타"),
                                    new CaseBuilder()
                                            .when(report.reportType.eq(ReportType.COMMENT))
                                            .then(JPAExpressions.select(comment1.adminControlType.stringValue())
                                                    .from(comment1)
                                                    .where(comment1.id.eq(report.reportedContentId)))
                                            .otherwise(JPAExpressions.select(board.adminControlType.stringValue())
                                                    .from(board)
                                                    .where(board.id.eq(report.reportedContentId))),
                                    new CaseBuilder()
                                            .when(report.reason.eq(BanReason.PROMOTIONAL_OR_ILLEGAL_ADS))
                                            .then("홍보")
                                            .when(report.reason.eq(BanReason.POLITICAL_CONTENT))
                                            .then("정치")
                                            .when(report.reason.eq(BanReason.SEXUAL_CONTENT))
                                            .then("음란")
                                            .when(report.reason.eq(BanReason.HARASSMENT))
                                            .then("비방")
                                            .otherwise("기타"),
                                    report.reportedContentId,
                                    member.nickname,
                                    new CaseBuilder()
                                            .when(report.reportType.eq(ReportType.COMMENT))
                                            .then(JPAExpressions.select(comment1.comment.substring(0, 10))
                                                    .from(comment1)
                                                    .where(comment1.id.eq(report.reportedContentId)))
                                            .otherwise(JPAExpressions.select(board.title)
                                                    .from(board)
                                                    .where(board.id.eq(report.reportedContentId))),
                                    report.createDate.stringValue(),
                                    report.id,
                                    Expressions.constant(StaticDataType.Report)
                            ))
                    .from(report)
                    .join(member)
                    .on(member.eq(report.reported))
                    .orderBy(report.createDate.desc())
                    .limit(10)
                    .offset(0)
                    .fetch();
            reportLatest.stream()
                    .forEach(x -> {
                        boolean readCheck = redisService.AdminReadCheck(admin.getPublicId().toString()
                                ,StaticDataType.Report, x.getContentId());
                        x.mappingCheckRead(readCheck);
                        x.updateCreateAt(
                                DateFormatUtil.formatByDot.format(
                                        LocalDateTime.parse(x.getCreateAt(), DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));

                        if (x.getMainStatus().equals("SHOW")) {
                            x.updateMainStatus("노출");
                        }
                        if (x.getMainStatus().equals("HIDDEN")) {
                            x.updateMainStatus("숨김");
                        }
                        if (x.getMainStatus().equals("PENDING")) {
                            x.updateMainStatus("보류");
                        }

                    });
         List<ResponseLatestData> inquiryLatest=queryFactory.select(
                            Projections.constructor(ResponseLatestData.class,
                                    Expressions.constant(""),
                                    new CaseBuilder()
                                            .when(inquiry.isAdminAnswered.isTrue())
                                            .then("답변완료")
                                            .otherwise("답변대기"),
                                    new CaseBuilder()
                                            .when(member.nickname.isNull())
                                            .then("비회원")
                                            .otherwise("회원"),
                                    inquiry.id,
                                    new CaseBuilder()
                                            .when(member.isNull())
                                            .then(inquiry.email)
                                            .otherwise(member.nickname),
                                    inquiry.content.substring(0, 20),
                                    inquiry.createdAt.stringValue(),
                                    Expressions.constant(StaticDataType.Inquiry)
                            ))
                    .from(inquiry)
                    .leftJoin(member)
                    .on(member.eq(inquiry.member))
                    .orderBy(inquiry.createdAt.desc())
                    .limit(10)
                    .offset(0)
                    .fetch();
            inquiryLatest.stream()
                    .forEach(x -> {

                        x.updateCreateAt(
                                DateFormatUtil.formatByDot.format(
                                        LocalDateTime.parse(x.getCreateAt(), DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));

                        boolean readCheck = redisService.AdminReadCheck(admin.getPublicId().toString()
                                , x.getStaticDataType(), x.getContentId());
                        x.mappingCheckRead(readCheck);
                    });
        List<ResponseLatestData> improveLatest=queryFactory.select(
                            Projections.constructor(ResponseLatestData.class,
                                    Expressions.constant(""),
                                    new CaseBuilder()
                                            .when(improvement.improvementStatus.eq(ImprovementStatus.COMPLETED))
                                            .then("완료")
                                            .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                            .then("접수")
                                            .otherwise("대기")
                                    ,
                                    improvement.title,
                                    improvement.id,
                                    member.nickname,
                                    improvement.content,
                                    improvement.createDate.stringValue(),
                                    Expressions.constant(StaticDataType.Improvement)

                            ))
                    .from(improvement)
                    .join(member)
                    .on(member.eq(improvement.member))
                    .orderBy(improvement.createDate.desc())
                    .limit(10)
                    .offset(0)
                    .fetch();
        improveLatest.stream()
                .forEach(x -> {
                    x.updateCreateAt(
                            DateFormatUtil.formatByDot.format(
                                    LocalDateTime.parse(x.getCreateAt(), DateFormatUtil.FLEXIBLE_NANO_FORMATTER)));
                    boolean readCheck = redisService.AdminReadCheck("ADMIN_ALARM", admin.getPublicId().toString()
                            , StaticDataType.Improvement, x.getContentId());
                    x.mappingCheckRead(readCheck);
                });
        latestDataResult.put(StaticDataType.Report.name(),reportLatest);
        latestDataResult.put(StaticDataType.Inquiry.name(),inquiryLatest);
        latestDataResult.put(StaticDataType.Improvement.name(),improveLatest);
        return latestDataResult;
    }

    private ResponseStatic makeInquiryImprovementStatic(DateType dateType,List<LocalDateTime> dateList){
        ResponseStatic improvementResponse = CreateStaticQueryFactory.createStaticQuery(
                improvement, dateType, dateList, queryFactory);
        ResponseStatic inquiryResponse = CreateStaticQueryFactory.createStaticQuery(
                inquiry, dateType, dateList, queryFactory);

        Map<String, Long> improvementMap = improvementResponse.getCurrentStaticData();
        Map<String, Long> inquiryMap = improvementResponse.getCurrentStaticData();
        Map<String, Long> finalMap = Stream.concat(inquiryMap.entrySet().stream(),
                        improvementMap.entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (oldValue, newValue) -> oldValue + newValue
                ));
        Map<String, Long> improvementPastMap = improvementResponse.getCurrentStaticData();
        Map<String, Long> inquiryPastMap = improvementResponse.getCurrentStaticData();
        Map<String, Long> finalPastMap = Stream.concat(inquiryPastMap.entrySet().stream(),
                        improvementPastMap.entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (oldValue, newValue) -> oldValue + newValue
                ));

        Long currentCount = improvementResponse.getCurrentCount() + inquiryResponse.getCurrentCount();
        Long pastCount = improvementResponse.getPastCount() + inquiryResponse.getPastCount();
        Long totCount = improvementResponse.getTotCount() + inquiryResponse.getTotCount();
        int totPercent = StaticUtil.makeStaticPercent(currentCount, pastCount);

        return ResponseStatic
                .builder()
                .currentStaticData(finalMap)
                .pastStaticData(finalPastMap)
                .percent(totPercent)
                .totCount(totCount)
                .currentCount(currentCount)
                .pastCount(pastCount)
                .staticDataName("InquiryImprovement")
                .build();
    }
}
