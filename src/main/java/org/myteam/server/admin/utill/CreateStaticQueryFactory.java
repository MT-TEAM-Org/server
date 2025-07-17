package org.myteam.server.admin.utill;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.report.domain.ReportType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.myteam.server.admin.dto.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.admin.entity.QAdminChangeLog.adminChangeLog;
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

    public static ResponseStatic createStaticQuery(List<LocalDateTime> dateList
            , JPAQueryFactory queryFactory, MemberStatus memberStatus) {

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long currentCount = queryFactory.select(adminChangeLog.count())
                .from(adminChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, adminChangeLog),
                        adminChangeLog.memberStatus.eq(memberStatus))
                .fetch()
                .get(0);
        Long pastCount = queryFactory.select(report.count())
                .from(report)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, adminChangeLog),
                        adminChangeLog.memberStatus.eq(memberStatus))
                .fetch()
                .get(0);

        Long totCount = queryFactory.select(adminChangeLog.count())
                .from(adminChangeLog)
                .where(adminChangeLog.memberStatus.eq(memberStatus))
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

    public static ResponseStatic createStaticQuery(List<LocalDateTime> dateList, JPAQueryFactory queryFactory,
                                                   AdminControlType adminControlType, StaticDataType staticDataType) {

        LocalDateTime staticStartTime = dateList.get(0);
        LocalDateTime staticEndTime = dateList.get(1);
        LocalDateTime staticStartTimePast = dateList.get(2);
        LocalDateTime staticEndTimePast = dateList.get(3);

        Long current_count = queryFactory
                .select(adminChangeLog.count())
                .from(adminChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTime, staticStartTime, adminChangeLog),
                        (adminChangeLog.adminControlType.eq(adminControlType)),
                        (adminChangeLog.staticDataType.eq(staticDataType)))
                .fetch().get(0);

        Long past_count = queryFactory
                .select(adminChangeLog.count())
                .from(adminChangeLog)
                .where(StaticUtil.betweenStaticTime(staticEndTimePast, staticStartTimePast, adminChangeLog),
                        (adminChangeLog.adminControlType.eq(adminControlType))
                        , (adminChangeLog.staticDataType.eq(staticDataType)))
                .fetch().get(0);

        Long tot_count = queryFactory.select(adminChangeLog.count())
                .from(adminChangeLog)
                .where((adminChangeLog.adminControlType.eq(adminControlType))
                        .and(adminChangeLog.staticDataType.eq(staticDataType)))
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


}
