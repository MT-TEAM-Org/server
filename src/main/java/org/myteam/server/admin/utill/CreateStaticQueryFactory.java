package org.myteam.server.admin.utill;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.naming.TransactionRef;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.DateType;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.report.domain.ReportType;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.admin.entity.QAdminContentChangeLog.adminContentChangeLog;
import static org.myteam.server.admin.entity.QAdminImproveChangeLog.adminImproveChangeLog;
import static org.myteam.server.admin.entity.QAdminInquiryChangeLog.adminInquiryChangeLog;
import static org.myteam.server.admin.entity.QAdminMemberChangeLog.*;
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
        List<Tuple> pastCount = queryFactory.select(groupByDate, ((EntityPathBase<?>) target).count())
                .from(target)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, target))
                .groupBy(groupByDate)
                .orderBy(groupByDate.desc())
                .fetch();

        Long totCount = queryFactory.select(((EntityPathBase<?>) target).count())
                .from(target)
                .fetch()
                .get(0);

        Map<String, Long> currentCountByDateFinal=new TreeMap<>();
        Map<String,Long> pastCountByDateFinal=new TreeMap<>();


        List<Long> values=makeFinalDateAndCountMap(currentCountByDateFinal,pastCountByDateFinal,currentCount
                ,pastCount,staticEndTime,staticStartTime,staticEndTimePast,staticStartTimePast, dateType);

        Long currentSums=values.get(0);
        Long pastSums=values.get(1);
        int percent = StaticUtil.makeStaticPercent(currentSums,pastSums);


        Class<?> entityClass = ((EntityPath<?>) target).getType();
        String name = entityClass.getSimpleName();

        return ResponseStatic.builder()
                .currentStaticData(currentCountByDateFinal)
                .pastStaticData(pastCountByDateFinal)
                .currentCount(currentSums)
                .pastCount(pastSums)
                .totCount(totCount)
                .percent(percent)
                .staticDataName(name.equals("Member") ? StaticDataType.UserSignIn.name() :name)
                .build();
    }

    public static ResponseStatic createSimpleStaticQuery(EntityPath<?> target,List<LocalDateTime> dateList
            , JPAQueryFactory queryFactory) {

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long currentCount = Optional.ofNullable(queryFactory.select(((EntityPathBase<?>) target).count())
                .from(target)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, target))
                .fetchOne()).orElse(0L);
        Long pastCount = Optional.ofNullable(queryFactory.select(((EntityPathBase<?>) target).count())
                .from(target)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, target))
                .fetchOne()).orElse(0L);

        Long totCount =Optional.ofNullable( queryFactory.select(((EntityPathBase<?>) target).count())
                .from(target)
                .fetchOne()).orElse(0L);

        int percent = StaticUtil.makeStaticPercent(currentCount,pastCount);
        Class<?> entityClass = ((EntityPath<?>) target).getType();
        String name = entityClass.getSimpleName();
        return ResponseStatic.builder()
                .currentCount(currentCount)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .staticDataName(name.equals("member") ? StaticDataType.UserSignIn.name() :name)
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
                .staticDataName(staticDataType.name())
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
                .staticDataName( isAnswered ? StaticDataType.InquiryComplete.name()
                        : StaticDataType.InquiryPending.name())
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
                .staticDataName(isMember ? StaticDataType.InquiryMember.name()
                        : StaticDataType.InquiryNoMember.name())
                .build();
    }

    public static ResponseStatic createStaticContentQuery(StaticDataType staticDataType,
                                                          AdminControlType adminControlType, List<LocalDateTime> dateList, JPAQueryFactory queryFactory)
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
                .staticDataName(staticDataType.name().equals("BOARD")?"HideBoard":"HideComment")
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
                .staticDataName(memberStatus.equals(MemberStatus.WARNED) ?StaticDataType.UserWarned.name()
                        :StaticDataType.UserBanned.name())
                .build();
    }

    public static ResponseStatic createReportStaticQuery(DateType dateType, List<LocalDateTime> dateList
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
        List<Tuple> pastCount = queryFactory.select(groupByDate, report.count())
                .from(report)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, report),
                        report.reportType.eq(reportType))
                .groupBy(groupByDate)
                .orderBy(groupByDate.desc())
                .fetch();

        Long totCount = queryFactory.select(report.count())
                .from(report)
                .where(report.reportType.eq(reportType))
                .fetch()
                .get(0);


        Map<String, Long> currentCountByDateFinal=new TreeMap<>();
        Map<String,Long> pastCountByDateFinal=new TreeMap<>();


        List<Long> values=makeFinalDateAndCountMap(currentCountByDateFinal,pastCountByDateFinal,currentCount
                ,pastCount,staticEndTime,staticStartTime,staticEndTimePast,staticStartTimePast, dateType);

        Long currentSums=values.get(0);
        Long pastSums=values.get(1);

        int percent = StaticUtil.makeStaticPercent(currentSums,pastSums);

        return ResponseStatic.builder()
                .currentStaticData(currentCountByDateFinal)
                .pastStaticData(pastCountByDateFinal)
                .currentCount(currentSums)
                .pastCount(pastSums)
                .totCount(totCount)
                .percent(percent)
                .staticDataName(reportType.equals(ReportType.BOARD) ? "ReportedBoard":"ReportedComment")
                .build();
    }

    public static ResponseStatic createSimpleReportStaticQuery( List<LocalDateTime> dateList
            , JPAQueryFactory queryFactory, ReportType reportType) {

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long currentCount = Optional.ofNullable(
                queryFactory.select(report.count())
                .from(report)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, report),
                        report.reportType.eq(reportType))
                .fetchOne())
                .orElse(0L);
        Long pastCount = Optional.ofNullable(queryFactory.select(report.count())
                .from(report)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, report),
                        report.reportType.eq(reportType))
                .fetchOne())
                .orElse(0L);

        Long totCount = queryFactory.select(report.count())
                .from(report)
                .where(report.reportType.eq(reportType))
                .fetch()
                .get(0);


        int percent = StaticUtil.makeStaticPercent(currentCount,pastCount);

        return ResponseStatic.builder()
                .currentCount(currentCount)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .staticDataName(reportType.equals(ReportType.BOARD) ? "ReportedBoard":"ReportedComment")
                .build();
    }


    public static ResponseStatic createSimpleUserDelStatic(List<LocalDateTime> dateList,JPAQueryFactory queryFactory){
        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);
        Long currentCount =Optional.ofNullable(queryFactory.select(member.count()).
                from(member)
                .where(StaticUtil.betweenStaticTimeDel(staticEndTime,staticStartTime))
                .fetchOne()).orElse(0L);
        Long pastCount =Optional.ofNullable( queryFactory.select( member.count()).
                from(member)
                .where(StaticUtil.betweenStaticTimeDel(staticEndTimePast,staticStartTimePast))
                .fetchOne()).orElse(0L);
        Long totCount = Optional.ofNullable(queryFactory.select(member.count())
                .from(member)
                .where(member.deleteAt.isNotNull())
                .fetchOne())
                .orElse(0L);
        int percent = StaticUtil.makeStaticPercent(currentCount,pastCount);
        return ResponseStatic
                .builder()
                .currentCount(currentCount)
                .pastCount(pastCount)
                .totCount(totCount)
                .percent(percent)
                .staticDataName(StaticDataType.UserDeleted.name())
                .build();
    }

    public static ResponseStatic createUserDelStatic(DateType dateType,List<LocalDateTime> dateList,JPAQueryFactory queryFactory){
        StringTemplate groupByDate = StaticUtil.delTemplate(dateType);
        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        List<Tuple> currentCount = queryFactory.select(groupByDate, member.count()).
                from(member)
                .where(StaticUtil.betweenStaticTimeDel(staticEndTime,staticStartTime))
                .groupBy(groupByDate)
                .orderBy(groupByDate.desc())
                .fetch();
        List<Tuple> pastCount = queryFactory.select(groupByDate, member.count()).
                from(member)
                .where(StaticUtil.betweenStaticTimeDel(staticEndTimePast,staticStartTimePast))
                .groupBy(groupByDate)
                .orderBy(groupByDate.desc())
                .fetch();
        Long totCount = queryFactory.select(member.count())
                .from(member)
                .where(member.deleteAt.isNotNull())
                .fetch().get(0);

        Map<String, Long> currentCountByDateFinal=new TreeMap<>();
        Map<String,Long> pastCountByDateFinal=new TreeMap<>();


        List<Long> values=makeFinalDateAndCountMap(currentCountByDateFinal,pastCountByDateFinal,currentCount
                ,pastCount,staticEndTime,staticStartTime,staticEndTimePast,staticStartTimePast, dateType);

        Long currentSums=values.get(0);
        Long pastSums=values.get(1);

        int percent = StaticUtil.makeStaticPercent(currentSums,pastSums);

        return ResponseStatic
                .builder()
                .currentStaticData(currentCountByDateFinal)
                .pastStaticData(pastCountByDateFinal)
                .currentCount(currentSums)
                .pastCount(pastSums)
                .totCount(totCount)
                .percent(percent)
                .staticDataName(StaticDataType.UserDeleted.name())
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

    private static void fillEmptyDate(LocalDateTime startTime,LocalDateTime endTime,Map<String,Long> maps
            ,DateType dateType,Map<String,Long> finalMap){
        if(dateType.equals(DateType.OneMonth)||dateType.equals(DateType.SixMonth)||
        dateType.equals(DateType.ThreeMonth)||dateType.equals(DateType.Year))
        {
            YearMonth startMonth = YearMonth.from(startTime);
            YearMonth endMonth = YearMonth.from(endTime);
            YearMonth current = startMonth;
            while (current.isBefore(endMonth)) {
                String monthKey = DateFormatUtil.formatByDotMonth.format(current);
                finalMap.put(monthKey, maps.getOrDefault(monthKey, 0L));
                current = current.plusMonths(1);
            }
        }
        else{
            LocalDateTime current=startTime;
            while (current.isBefore(endTime)) {
                String dayKey =DateFormatUtil.formatByDot.format(current);
                finalMap.put(dayKey,maps.getOrDefault(dayKey, 0L));
                current = current.plusDays(1L);
            }
        }
    }

    private static void fillDateAndCountToMap(Map<String,Long> map,List<Tuple> data){
        data.stream()
                .forEach(x->{
                    map.put(x.get(0,String.class),x.get(1,Long.class));
                });
    }
    private static Long makeTotalSumOfCount(List<Tuple> data){
        return data.stream()
                .mapToLong(
                        x->{
                            return x.get(1,Long.class);
                        }
                )
                .sum();
    }
    private static List<Long> makeFinalDateAndCountMap
            (Map<String, Long> currentCountByDateFinal,Map<String, Long> pastCountByDateFinal
                    ,List<Tuple> currentCount,List<Tuple> pastCount,
             LocalDateTime staticEndTime,LocalDateTime staticStartTime,LocalDateTime staticEndTimePast
                    ,LocalDateTime staticStartTimePast,DateType dateType){
        Map<String, Long> currentCountByDate = new HashMap<>();
        Map<String,Long> pastCountByDate=new HashMap<>();
        fillDateAndCountToMap(currentCountByDate,currentCount);
        fillDateAndCountToMap(pastCountByDate,pastCount);
        fillEmptyDate(staticEndTime,staticStartTime,currentCountByDate,dateType,currentCountByDateFinal);
        fillEmptyDate(staticEndTimePast,staticStartTimePast,pastCountByDate,dateType,pastCountByDateFinal);
        List<Long> values=new ArrayList<>();
        values.add(makeTotalSumOfCount(currentCount));
        values.add(makeTotalSumOfCount(pastCount));

        return values;
    }

}
