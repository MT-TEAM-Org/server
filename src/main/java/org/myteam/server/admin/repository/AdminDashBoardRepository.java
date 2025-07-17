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
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.ReportType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.myteam.server.admin.dto.AdminBashBoardRequestDto.RequestLatestData;
import static org.myteam.server.admin.dto.AdminBashBoardRequestDto.RequestStatic;
import static org.myteam.server.admin.dto.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.admin.entity.QAdminChangeLog.adminChangeLog;
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


    public ResponseStatic getStaticData(RequestStatic requestStatic) {

        DateType dateType = requestStatic.getDateType();
        StaticDataType staticDataType = requestStatic.getStaticDataType();

        return getStaticDataByRequest(dateType, staticDataType);
    }

    private ResponseStatic getStaticDataByRequest(DateType dateType, StaticDataType staticDataType) {
        LocalDateTime now = LocalDateTime.now();

        List<LocalDateTime> dateList = DateTypeFactory.SupplyDateTime(dateType, now);

        LocalDateTime static_start_time = dateList.get(0);
        LocalDateTime static_end_time = dateList.get(1);
        LocalDateTime static_start_time2 = dateList.get(2);
        LocalDateTime static_end_time2 = dateList.get(3);

        if (staticDataType.name().equals(StaticDataType.BOARD.name())) {
            return CreateStaticQueryFactory.createStaticQuery(board, dateType, dateList, queryFactory);
        }
        if (staticDataType.name().equals(StaticDataType.COMMENT.name())) {
            return CreateStaticQueryFactory.createStaticQuery(comment1, dateType, dateList, queryFactory);
        }
        if (staticDataType.name().equals(StaticDataType.UserSignIn.name())) {
            return CreateStaticQueryFactory.createStaticQuery(member, dateType, dateList, queryFactory);
        }
        if (staticDataType.name().equals(StaticDataType.UserAccess.name())) {
            return CreateStaticQueryFactory.createStaticQuery(memberAccess, dateType, dateList, queryFactory);
        }
        if (staticDataType.name().equals(StaticDataType.ImprovementInquiry.name())) {

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
            Long currentCount = improvementResponse.getCurrentCount() + inquiryResponse.getCurrentCount();
            Long pastCount = improvementResponse.getPastCount() + inquiryResponse.getPastCount();
            Long totCount = improvementResponse.getTotCount() + inquiryResponse.getTotCount();
            int totPercent = StaticUtil.makeStaticPercent(currentCount, pastCount);

            return ResponseStatic
                    .builder()
                    .currentStaticData(finalMap)
                    .percent(totPercent)
                    .totCount(totCount)
                    .currentCount(currentCount)
                    .pastCount(pastCount)
                    .build();

        }
        if (staticDataType.name().equals(StaticDataType.ReportedBoard.name())
                || staticDataType.name().equals(StaticDataType.ReportedComment.name())) {

            ReportType reportType = ReportType.BOARD;
            if (staticDataType.name().equals(StaticDataType.ReportedComment.name())) {
                reportType = ReportType.COMMENT;
            }
            return CreateStaticQueryFactory.createStaticQuery(dateType, dateList, queryFactory, reportType);
        }

        if (staticDataType.name().equals(StaticDataType.UserDeleted.name())) {

            StringTemplate groupByDate = StaticUtil.delTemplate(dateType);

            List<Tuple> current_count = queryFactory.select(groupByDate, member.count()).
                    from(member)
                    .where(StaticUtil.betweenStaticTimeDel(static_end_time, static_start_time))
                    .groupBy(groupByDate)
                    .orderBy(groupByDate.desc())
                    .fetch();
            Long past_count = queryFactory.select(member.count()).
                    from(member)
                    .where(StaticUtil.betweenStaticTimeDel(static_end_time2, static_start_time2))
                    .fetch().get(0);

            Long tot_count = queryFactory.select(member.count())
                    .from(member)
                    .where(member.deleteAt.isNotNull())
                    .fetch().get(0);


            Map<String, Long> currentStataicData = new HashMap<>();

            current_count.stream()
                    .forEach(x -> {
                        currentStataicData.put(x.get(0, String.class), x.get(1, Long.class));
                    });

            Long sums = current_count
                    .stream()
                    .mapToLong(x -> {
                        return x.get(1, Long.class);
                    })
                    .sum();

            int percent = StaticUtil.makeStaticPercent(sums, past_count);

            return ResponseStatic
                    .builder()
                    .currentStaticData(currentStataicData)
                    .currentCount(sums)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();
        }

        if (staticDataType.name().equals(StaticDataType.UserWarned.name())) {
            Long current_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time, static_start_time, adminChangeLog),
                            (adminChangeLog.memberStatus.eq(MemberStatus.PENDING)))
                    .fetch().get(0);

            Long past_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time2, static_start_time2, adminChangeLog),
                            (adminChangeLog.memberStatus.eq(MemberStatus.PENDING)))
                    .fetch().get(0);

            Long tot_count = queryFactory.select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(adminChangeLog.memberStatus.eq(MemberStatus.PENDING))
                    .fetch().get(0);


            int percent = StaticUtil.makeStaticPercent(current_count, past_count);

            return ResponseStatic
                    .builder()
                    .currentCount(current_count)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();
        }
        if (staticDataType.name().equals(StaticDataType.UserBanned.name())) {
            Long current_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time, static_start_time, adminChangeLog),
                            (adminChangeLog.memberStatus.eq(MemberStatus.INACTIVE)))
                    .fetch().get(0);

            Long past_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time2, static_start_time2, adminChangeLog),
                            (adminChangeLog.memberStatus.eq(MemberStatus.INACTIVE)))
                    .fetch().get(0);

            Long tot_count = queryFactory.select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(adminChangeLog.memberStatus.eq(MemberStatus.INACTIVE))
                    .fetch().get(0);

            int percent = StaticUtil.makeStaticPercent(current_count, past_count);

            return ResponseStatic
                    .builder()
                    .currentCount(current_count)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();
        }

        if (staticDataType.name().equals(StaticDataType.HideComment.name())) {
            Long current_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time, static_start_time, adminChangeLog),
                            (adminChangeLog.adminControlType.eq(AdminControlType.HIDDEN)),
                            (adminChangeLog.staticDataType.eq(StaticDataType.COMMENT)))
                    .fetch().get(0);

            Long past_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time2, static_start_time2, adminChangeLog),
                            (adminChangeLog.adminControlType.eq(AdminControlType.HIDDEN))
                            , (adminChangeLog.staticDataType.eq(StaticDataType.COMMENT)))
                    .fetch().get(0);

            Long tot_count = queryFactory.select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where((adminChangeLog.adminControlType.eq(AdminControlType.HIDDEN))
                            .and(adminChangeLog.staticDataType.eq(StaticDataType.COMMENT)))
                    .fetch().get(0);

            int percent = StaticUtil.makeStaticPercent(current_count, past_count);

            return ResponseStatic
                    .builder()
                    .currentCount(current_count)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();
        }

        if (staticDataType.name().equals(StaticDataType.HideBoard.name())) {
            Long current_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time, static_start_time, adminChangeLog),
                            (adminChangeLog.adminControlType.eq(AdminControlType.HIDDEN))
                            , (adminChangeLog.staticDataType.eq(StaticDataType.BOARD)))
                    .fetch().get(0);

            Long past_count = queryFactory
                    .select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where(StaticUtil.betweenStaticTime(static_end_time2, static_start_time2, adminChangeLog),
                            (adminChangeLog.adminControlType.eq(AdminControlType.HIDDEN))
                            , (adminChangeLog.staticDataType.eq(StaticDataType.BOARD)))
                    .fetch().get(0);

            Long tot_count = queryFactory.select(adminChangeLog.count())
                    .from(adminChangeLog)
                    .where((adminChangeLog.adminControlType.eq(AdminControlType.HIDDEN))
                            , (adminChangeLog.staticDataType.eq(StaticDataType.BOARD)))
                    .fetch().get(0);

            int percent = StaticUtil.makeStaticPercent(current_count, past_count);

            return ResponseStatic
                    .builder()
                    .currentCount(current_count)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();
        }


        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER, "없는 형식의 파라미터 입니다");
    }


    public List<ResponseLatestData> getLatestData(RequestLatestData requestLatestData) {

        Member admin = securityReadService.getMember();

        if (requestLatestData.getStaticDataType().name().equals(StaticDataType.Report.name())) {

            List<ResponseLatestData> responseLatestDataList = queryFactory.select(
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
                                    Expressions.constant(StaticDataType.Report),
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
                                    report.createDate
                            ))
                    .from(report)
                    .join(member)
                    .on(member.eq(report.reported))
                    .orderBy(report.createDate.desc())
                    .limit(10)
                    .offset(0)
                    .fetch();

            responseLatestDataList.stream()
                    .forEach(x -> {
                        boolean readCheck = redisService.AdminReadCheck("ADMIN_ALARM", admin.getPublicId().toString()
                                , x.getStaticDataType(), x.getContentId());
                        x.mappingCheckRead(readCheck);

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

            return responseLatestDataList;
        }

        if (requestLatestData.getStaticDataType().name().equals(StaticDataType.Inquiry.name())) {


            List<ResponseLatestData> responseLatestDataList = queryFactory.select(
                            Projections.constructor(ResponseLatestData.class,
                                    Expressions.constant(""),
                                    Expressions.constant(StaticDataType.Inquiry),
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
                                            .when(member.nickname.isNull())
                                            .then(member.email)
                                            .otherwise(member.nickname),
                                    inquiry.content.substring(0, 20),
                                    inquiry.createdAt
                            ))
                    .from(inquiry)
                    .join(member)
                    .on(member.eq(inquiry.member))
                    .orderBy(inquiry.createdAt.desc())
                    .limit(10)
                    .offset(0)
                    .fetch();
            responseLatestDataList.stream()
                    .forEach(x -> {
                        boolean readCheck = redisService.AdminReadCheck("ADMIN_ALARM", admin.getPublicId().toString()
                                , x.getStaticDataType(), x.getContentId());
                        x.mappingCheckRead(readCheck);
                    });

            return responseLatestDataList;
        }
        if (requestLatestData.getStaticDataType().name().equals(StaticDataType.Improvement.name())) {


            List<ResponseLatestData> responseLatestDataList = queryFactory.select(
                            Projections.constructor(ResponseLatestData.class,
                                    Expressions.constant(""),
                                    Expressions.constant(StaticDataType.Improvement),
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
                                    improvement.createDate

                            ))
                    .from(improvement)
                    .join(member)
                    .on(member.eq(improvement.member))
                    .orderBy(improvement.createDate.desc())
                    .limit(10)
                    .offset(0)
                    .fetch();


            responseLatestDataList.stream()
                    .forEach(x -> {
                        boolean readCheck = redisService.AdminReadCheck("ADMIN_ALARM", admin.getPublicId().toString()
                                , x.getStaticDataType(), x.getContentId());
                        x.mappingCheckRead(readCheck);
                    });

            return responseLatestDataList;
        }

        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER, "없는 형식의 파라미터 입니다");
    }

}
