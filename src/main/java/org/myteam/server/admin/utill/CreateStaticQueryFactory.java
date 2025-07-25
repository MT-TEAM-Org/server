package org.myteam.server.admin.utill;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.domain.QImprovement;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.report.domain.ReportType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.myteam.server.admin.dto.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.improvement.domain.QImprovement.*;
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

        Long currentCount = queryFactory.select(improvement.count())
                .from(improvement)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, improvement),
                        improvementProcessCond(staticDataType))
                .fetch()
                .get(0);
        Long pastCount = queryFactory.select(improvement.count())
                .from(improvement)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, improvement),
                        improvementProcessCond(staticDataType))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(improvement.count())
                .from(improvement)
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

        Long currentCount = queryFactory.select(inquiry.count())
                .from(inquiry)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime,inquiry),
                        inquiry.isAdminAnswered.eq(isAnswered))
                .fetch()
                .get(0);
        Long pastCount = queryFactory.select(inquiry.count())
                .from(inquiry)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast,inquiry),
                        inquiry.isAdminAnswered.eq(isAnswered))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(inquiry.count())
                .from(inquiry)
                .where(inquiry.isAdminAnswered.eq(isAnswered))
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

        Long currentCount = queryFactory.select(inquiry.count())
                .from(inquiry)
                .join(member)
                .on(member.eq(inquiry.member))
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime,inquiry),
                        inquiryIsMember(isMember))
                .fetch()
                .get(0);
        Long pastCount =queryFactory.select(inquiry.count())
                .from(inquiry)
                .join(member)
                .on(member.eq(inquiry.member))
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast,inquiry),
                        inquiryIsMember(isMember))
                .fetch()
                .get(0);
        Long totCount = queryFactory.select(inquiry.count())
                .from(inquiry)
                .join(member)
                .on(member.eq(inquiry.member))
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
            return member.nickname.isNotNull();
        }
        return member.nickname.isNull();
    }

    private static Predicate improvementProcessCond(StaticDataType staticDataType){
        if(staticDataType.equals(StaticDataType.ImprovementPending)){
            return improvement.improvementStatus.eq(ImprovementStatus.PENDING);
        }
        if(staticDataType.equals(StaticDataType.ImprovementReceived)){
            return improvement.improvementStatus.eq(ImprovementStatus.RECEIVED);
        }
        return improvement.improvementStatus.eq(ImprovementStatus.COMPLETED);
    }


}
