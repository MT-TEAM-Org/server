package org.myteam.server.admin.utill;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.myteam.server.admin.entity.AdminInquiryChangeLog;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.report.domain.ReportType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.admin.entity.QAdminContentChangeLog.adminContentChangeLog;
import static org.myteam.server.admin.entity.QAdminImproveChangeLog.adminImproveChangeLog;
import static org.myteam.server.admin.entity.QAdminInquiryChangeLog.adminInquiryChangeLog;
import static org.myteam.server.admin.entity.QAdminMemberChangeLog.*;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.report.domain.QReport.report;


public class CreateStaticQueryFactory {

    public static ResponseStatic createStaticQuery(EntityPath<?> target,
                                                   DateType dateType, List<LocalDateTime> dateList
            , JPAQueryFactory queryFactory) {

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        StringTemplate groupByDate = StaticUtil.dateTemplate(dateType, target);
        List<Tuple> currentCount = queryFactory.select(groupByDate, ((EntityPathBase<?>) target).count())
                .from(target)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, target))
                .groupBy(groupByDate)
                .orderBy(groupByDate.desc())
                .fetch();
        Long pastCount = queryFactory.select(((EntityPathBase<?>) target).count())
                .from(target)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, target))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(((EntityPathBase<?>) target).count())
                .from(target)
                .fetch()
                .get(0);

        Map<String, Long> currentCountByDate = new HashMap<>();

        currentCount.stream()
                .forEach(x -> {
                    currentCountByDate.put(x.get(0, String.class), x.get(1, Long.class));
                });
        Long sums = currentCount.stream()
                .mapToLong(x -> x.get(1, Long.class))
                .sum();
        int percent = StaticUtil.makeStaticPercent(sums, pastCount);

        return ResponseStatic.builder()
                .currentStaticData(currentCountByDate)
                .currentCount(sums)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .build();

    }

    public static ResponseStatic createImprovementStaticQuery(List<LocalDateTime> dateList
            ,StaticDataType staticDataType, JPAQueryFactory queryFactory){

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long currentCount = queryFactory.select(adminImproveChangeLog.id.countDistinct())
                .from(adminImproveChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime,adminImproveChangeLog),
                        improvementProcessCond(staticDataType))
                .fetch()
                .get(0);
        Long pastCount = queryFactory.select(adminImproveChangeLog.id.countDistinct())
                .from(adminImproveChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast,adminImproveChangeLog),
                        improvementProcessCond(staticDataType))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(adminImproveChangeLog.id.countDistinct())
                .from(adminImproveChangeLog)
                .where(improvementProcessCond(staticDataType))
                .fetch()
                .get(0);
        int percent = StaticUtil.makeStaticPercent(currentCount, pastCount);
        return ResponseStatic.builder()
                .currentCount(currentCount)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .build();
    }

    public static ResponseStatic createInquiryStaticQuery(List<LocalDateTime> dateList
            , Boolean isAnswered, JPAQueryFactory queryFactory){

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long currentCount = queryFactory.select(adminInquiryChangeLog.id.countDistinct())
                .from(adminInquiryChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime,adminInquiryChangeLog),
                        adminInquiryChangeLog.isAnswered.eq(isAnswered))
                .fetch()
                .get(0);
        Long pastCount = queryFactory.select(adminInquiryChangeLog.id.countDistinct())
                .from(adminInquiryChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast,adminInquiryChangeLog),
                    adminInquiryChangeLog.isAnswered.eq(isAnswered))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(adminInquiryChangeLog.id.countDistinct())
                .from(adminInquiryChangeLog)
                .where(adminInquiryChangeLog.isAnswered.eq(isAnswered))
                .fetch()
                .get(0);
        int percent = StaticUtil.makeStaticPercent(currentCount, pastCount);
        return ResponseStatic.builder()
                .currentCount(currentCount)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .build();
    }

    public static ResponseStatic createMemberInquiryStaticQuery(List<LocalDateTime> dateList
            , Boolean isMember, JPAQueryFactory queryFactory){

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long currentCount = queryFactory.select(adminInquiryChangeLog.id.countDistinct())
                .from(adminInquiryChangeLog)
                .join(member)
                .on(member.eq(adminInquiryChangeLog.admin))
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime,adminInquiryChangeLog),
                        inquiryIsMember(isMember))
                .fetch()
                .get(0);
        Long pastCount =queryFactory.select(adminInquiryChangeLog.id.countDistinct())
                .from(adminInquiryChangeLog)
                .join(member)
                .on(member.eq(adminInquiryChangeLog.admin))
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast,adminInquiryChangeLog),
                        inquiryIsMember(isMember))
                .fetch()
                .get(0);
        Long totCount = queryFactory.select(adminInquiryChangeLog.id.countDistinct())
                .from(adminInquiryChangeLog)
                .join(member)
                .on(member.eq(adminInquiryChangeLog.admin))
                .where(inquiryIsMember(isMember))
                .fetch()
                .get(0);
        int percent = StaticUtil.makeStaticPercent(currentCount, pastCount);
        return ResponseStatic.builder()
                .currentCount(currentCount)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .build();
    }

    public static ResponseStatic createStaticContentQuery(StaticDataType staticDataType,
                                                          AdminControlType adminControlType,List<LocalDateTime> dateList,JPAQueryFactory queryFactory)
    {
        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long current_count = queryFactory
                .select(adminContentChangeLog.contentId.countDistinct())
                .from(adminContentChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTime,staticStartTime,adminContentChangeLog),
                        (adminContentChangeLog.adminControlType.eq(adminControlType)),
                        (adminContentChangeLog.staticDataType.eq(staticDataType)))
                .fetch().get(0);

        Long past_count = queryFactory
                .select(adminContentChangeLog.contentId.countDistinct())
                .from(adminContentChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast,staticStartTimePast,adminContentChangeLog),
                        (adminContentChangeLog.adminControlType.eq(adminControlType))
                        , (adminContentChangeLog.staticDataType.eq(staticDataType)))
                .fetch().get(0);

        Long tot_count = queryFactory.select(adminContentChangeLog
                        .contentId.countDistinct())
                .from(adminContentChangeLog)
                .where((adminContentChangeLog.adminControlType.eq(adminControlType))
                        .and(adminContentChangeLog.staticDataType.eq(staticDataType)))
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

    public static ResponseStatic createStaticMemberStatusQuery(MemberStatus memberStatus,List<LocalDateTime> dateList,JPAQueryFactory queryFactory)
    {
        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long current_count = queryFactory
                .select(adminMemberChangeLog.memberId.countDistinct())
                .from(adminMemberChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTime,staticStartTime,
                                adminMemberChangeLog),
                        (adminMemberChangeLog.memberStatus.eq(memberStatus)))
                .fetch().get(0);

        Long past_count = queryFactory
                .select(adminMemberChangeLog.memberId.countDistinct())
                .from(adminMemberChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast,staticStartTimePast,
                               adminMemberChangeLog),
                        (adminMemberChangeLog.memberStatus.eq(memberStatus)))
                .fetch().get(0);

        Long tot_count = queryFactory.select(adminMemberChangeLog.memberId.countDistinct())
                .from(adminMemberChangeLog)
                .where(adminMemberChangeLog.memberStatus.eq(memberStatus))
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

    public static ResponseStatic createStaticQuery(DateType dateType, List<LocalDateTime> dateList
            , JPAQueryFactory queryFactory, ReportType reportType) {

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        StringTemplate groupByDate = StaticUtil.dateTemplate(dateType, report);
        List<Tuple> currentCount = queryFactory.select(groupByDate, report.count())
                .from(report)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, report),
                        report.reportType.eq(reportType))
                .groupBy(groupByDate)
                .orderBy(groupByDate.desc())
                .fetch();
        Long pastCount = queryFactory.select(report.count())
                .from(report)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, report),
                        report.reportType.eq(reportType))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(report.count())
                .from(report)
                .where(report.reportType.eq(reportType))
                .fetch()
                .get(0);

        Map<String, Long> currentCountByDate = new HashMap<>();

        currentCount.stream()
                .forEach(x -> {
                    currentCountByDate.put(x.get(0, String.class), x.get(1, Long.class));
                });
        Long sums = currentCount.stream()
                .mapToLong(x -> x.get(1, Long.class))
                .sum();
        int percent = StaticUtil.makeStaticPercent(sums, pastCount);

        return ResponseStatic.builder()
                .currentStaticData(currentCountByDate)
                .currentCount(sums)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .build();
    }

    private static Predicate inquiryIsMember(boolean isMember){
       if(isMember){
            return adminInquiryChangeLog.isMember.isTrue();
        }
        return adminInquiryChangeLog.isMember.isFalse();
    }

    private static Predicate improvementProcessCond(StaticDataType staticDataType){
        if(staticDataType.equals(StaticDataType.ImprovementPending)){
            return adminImproveChangeLog.improvementStatus.eq(ImprovementStatus.PENDING);
        }
        if(staticDataType.equals(StaticDataType.ImprovementReceived)){
            return adminImproveChangeLog.improvementStatus.eq(ImprovementStatus.RECEIVED);
        }
        return adminImproveChangeLog.improvementStatus.eq(ImprovementStatus.COMPLETED);
    }


}
