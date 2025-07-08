package org.myteam.server.admin.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.utill.DateType;
import org.myteam.server.admin.utill.DateTypeFactory;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.admin.utill.StaticUtil;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.ReportType;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.myteam.server.admin.dto.AdminDashBorad.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.member.entity.QMemberAccess.memberAccess;
import static org.myteam.server.report.domain.QReport.report;

@Repository
@RequiredArgsConstructor
public class AdminDashBoardRepository {

    private final JPAQueryFactory queryFactory;
    private final RedisService redisService;
    private final SecurityReadService securityReadService;


    public ResponseStatic getStaticData(RequestStatic requestStatic){

        DateType dateType=requestStatic.getDateType();
        StaticDataType staticDataType=requestStatic.getStaticDataType();

        return getStaticDataByRequest(dateType,staticDataType);
    }


    private ResponseStatic getStaticDataByRequest(DateType dateType,StaticDataType staticDataType){
        LocalDateTime now=LocalDateTime.now();

        List<LocalDateTime> dateList=DateTypeFactory.SupplyDateTime(dateType,now);

        LocalDateTime static_start_time=dateList.get(0);
        LocalDateTime static_end_time=dateList.get(1);
        LocalDateTime static_start_time2=dateList.get(2);
        LocalDateTime static_end_time2=dateList.get(3);


        if(staticDataType.name().equals(StaticDataType.Comment.name())){
            StringTemplate groupByDate=StaticUtil.dateTemplate(dateType,comment1);
           List<Tuple> currentCount=queryFactory.select(groupByDate,comment1.count())
                    .from(comment1)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,comment1))
                    .groupBy(groupByDate)
                   .orderBy(groupByDate.desc())
                    .fetch();
           Long pastCount=queryFactory.select(comment1.count())
                    .from(comment1)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,comment1))
                    .fetch()
                    .get(0);

            Long totCount=queryFactory.select(comment1.count())
                    .from(comment1)
                    .fetch()
                    .get(0);

            Map<String,Long> currentCountByDate=new HashMap<>();



            currentCount.stream()
                    .forEach(x->{
                        currentCountByDate.put(x.get(0,String.class),x.get(1,Long.class));
                    });
            Long sums=currentCount.stream()
                    .mapToLong(x->x.get(1,Long.class))
                    .sum();
            int percent=StaticUtil.make_static_percent(sums,pastCount);

            return ResponseStatic.builder()
                    .currentStaticData(currentCountByDate)
                    .currentCount(sums)
                    .pastCount(pastCount)
                    .totCount(totCount)
                    .percent(percent)
                    .build();
        }
        if(staticDataType.name().equals(StaticDataType.Board.name())){

            StringTemplate groupByDate=StaticUtil.dateTemplate(dateType,board);

            List<Tuple> currentCount=queryFactory.select(groupByDate,board.count())
                    .from(board)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,board))
                    .groupBy(groupByDate)
                    .orderBy(groupByDate.desc())
                    .fetch();
            Long pastCount=queryFactory.select(board.count())
                    .from(board)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,board))
                    .fetch()
                    .get(0);

            Long totCount=queryFactory.select(board.count())
                    .from(board)
                    .fetch()
                    .get(0);
            Map<String,Long> currentStaticData=new HashMap<>();

            currentCount.stream()
                    .forEach(x->
                            currentStaticData.put(x.get(0,String.class),x.get(1,Long.class))
                            );
            Long sums=currentCount.stream()
                    .mapToLong(x->{
                       return x.get(1,Long.class);
                    })
                    .sum();

            int percent=StaticUtil.make_static_percent(sums,pastCount);

            return ResponseStatic.builder()
                    .currentStaticData(currentStaticData)
                    .currentCount(sums)
                    .pastCount(pastCount)
                    .totCount(totCount)
                    .percent(percent)
                    .build();

        }
        if(staticDataType.name().equals(StaticDataType.ReportedBoard.name())
                || staticDataType.name().equals(StaticDataType.ReportedComment.name())){

            StringTemplate groupByDate=StaticUtil.dateTemplate(dateType,report);
            ReportType reportType;
            reportType=ReportType.COMMENT;
            if(staticDataType.name().equals(StaticDataType.ReportedBoard.name())){
                reportType=ReportType.BOARD;
            }


            List<Tuple> currentCount=queryFactory.select(groupByDate,report.count())
                    .from(report)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,report),report_feat_report_type(reportType))
                    .groupBy(groupByDate)
                    .orderBy(groupByDate.desc())
                    .fetch();
            Long pastCount=queryFactory.select(report.count())
                    .from(report)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,report),report_feat_report_type(reportType))
                    .fetch()
                    .get(0);

            Long totCount=queryFactory.select(report.count())
                    .from(report)
                    .where(report.reportType.eq(reportType))
                    .fetch()
                    .get(0);

            Map<String,Long> currentStaticData=new HashMap<>();

            currentCount.stream().forEach(
                    x->{
                        currentStaticData.put(x.get(0,String.class),x.get(1,Long.class));
                    }
            );

            Long sums=currentCount.stream()
                    .mapToLong(x->{

                        return x.get(1,Long.class);
                    })
                    .sum();

            int percent=StaticUtil.make_static_percent(sums,pastCount);

            return ResponseStatic
                    .builder()
                    .currentStaticData(currentStaticData)
                    .currentCount(sums)
                    .pastCount(pastCount)
                    .totCount(totCount)
                    .percent(percent)
                    .build();

        }

        if(staticDataType.name().equals(StaticDataType.ImprovementInquiry.name())){


            StringTemplate groupByDateImp=StaticUtil.dateTemplate(dateType,improvement);
            StringTemplate groupByDateInq=StaticUtil.dateTemplate(dateType,inquiry);

            List<Tuple> inquiryCurrentCount=queryFactory.select(groupByDateInq,inquiry.count())
                    .from(inquiry)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,inquiry))
                    .groupBy(groupByDateInq)
                    .orderBy(groupByDateInq.desc())
                    .fetch();
            Long inquiryPastCount=queryFactory.select(inquiry.count())
                    .from(inquiry)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,inquiry))
                    .fetch()
                    .get(0);

            Long inquiryTotCount=queryFactory.select(inquiry.count())
                    .from(inquiry)
                    .fetch()
                    .get(0);

            List<Tuple> improvementCurrentCount=queryFactory.select(groupByDateImp,improvement.count())
                    .from(improvement)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,improvement))
                    .groupBy(groupByDateImp)
                    .orderBy(groupByDateImp.desc())
                    .fetch();
            Long improvementPastCount=queryFactory.select(improvement.count())
                    .from(improvement)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,improvement))
                    .fetch()
                    .get(0);

            Long improvementTotCount=queryFactory.select(improvement.count())
                    .from(improvement)
                    .fetch()
                    .get(0);


           List<Tuple> totTuples=new ArrayList<>();


           Map<String,Long> currentStaticData=new HashMap<>();

           totTuples.addAll(improvementCurrentCount);
           totTuples.addAll(inquiryCurrentCount);

            Long currentCount=totTuples.stream().
                    mapToLong(x->{
                        return x.get(1,Long.class);
                    })
                    .sum();
            totTuples.stream()
                    .forEach(x->{
                        currentStaticData.computeIfPresent(x.get(0,String.class),(key,value)->value+x.get(1,Long.class));
                        currentStaticData.computeIfAbsent(x.get(0,String.class),key->x.get(1,Long.class));
                    });


            Long pastCount=inquiryPastCount+improvementPastCount;

            int percent=StaticUtil.make_static_percent(currentCount,pastCount);


            return ResponseStatic
                    .builder()
                    .currentStaticData(currentStaticData)
                    .currentCount(currentCount)
                    .pastCount(pastCount)
                    .totCount(improvementTotCount+inquiryTotCount)
                    .percent(percent)
                    .build();

        }
        if(staticDataType.name().equals(StaticDataType.UserSignIn.name())){

            StringTemplate groupByDate=StaticUtil.dateTemplate(dateType,member);

            List<Tuple> current_count=queryFactory.select(groupByDate,member.count())
                    .from(member)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,member))
                    .groupBy(groupByDate)
                    .orderBy(groupByDate.desc())
                    .fetch();
            Long past_count=queryFactory.select(member.count())
                    .from(member)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,member))
                    .fetch().get(0);

            Long tot_count=queryFactory.select(member.count())
                    .from(member)
                    .fetch().get(0);

            Map<String,Long> currentStataicData=new HashMap<>();

            current_count.stream()
                    .forEach(x->{

                        currentStataicData.put(x.get(0,String.class),x.get(1,Long.class));
                    });

            Long sums=current_count
                    .stream()
                    .mapToLong(x->{
                        return x.get(1,Long.class);
                    })
                    .sum();

            int percent=StaticUtil.make_static_percent(sums,past_count);

            return ResponseStatic
                    .builder()
                    .currentStaticData(currentStataicData)
                    .percent(percent)
                    .currentCount(sums)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .build();

        }

        if(staticDataType.name().equals(StaticDataType.UserDeleted.name())){

            StringTemplate groupByDate=StaticUtil.delTemplate(dateType);

            List<Tuple> current_count=queryFactory.select(groupByDate,member.count()).
                    from(member)
                    .where(StaticUtil.between_static_time_del(static_end_time,static_start_time))
                    .groupBy(groupByDate)
                    .orderBy(groupByDate.desc())
                    .fetch();
            Long past_count=queryFactory.select(member.count()).
                    from(member)
                    .where(StaticUtil.between_static_time_del(static_end_time2,static_start_time2))
                    .fetch().get(0);

            Long tot_count=queryFactory.select(member.count())
                    .from(member)
                    .where(member.deleteAt.isNotNull())
                    .fetch().get(0);


            Map<String,Long> currentStataicData=new HashMap<>();

            current_count.stream()
                    .forEach(x->{

                        currentStataicData.put(x.get(0,String.class),x.get(1,Long.class));
                    });

            Long sums=current_count
                    .stream()
                    .mapToLong(x->{
                        return x.get(1,Long.class);
                    })
                    .sum();




            int percent=StaticUtil.make_static_percent(sums,past_count);

            return ResponseStatic
                    .builder()
                    .currentStaticData(currentStataicData)
                    .currentCount(sums)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();
        }
        if(staticDataType.name().equals(StaticDataType.UserAccess.name())){



            StringTemplate groupByDate=StaticUtil.dateTemplate(dateType,memberAccess);



            List<Tuple> current_count=queryFactory.select(groupByDate,memberAccess.count())
                    .from(memberAccess)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,memberAccess))
                    .groupBy(groupByDate)
                    .orderBy(groupByDate.desc())
                    .fetch();
            Long past_count=queryFactory.select(memberAccess.count())
                    .from(memberAccess)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,memberAccess))
                    .fetch().get(0);

            Long tot_count=queryFactory.select(memberAccess.count())
                    .from(memberAccess)
                    .fetch().get(0);

            Map<String,Long> currentStataicData=new HashMap<>();

            current_count.stream()
                    .forEach(x->{

                        currentStataicData.put(x.get(0,String.class),x.get(1,Long.class));
                    });

            Long sums=current_count
                    .stream()
                    .mapToLong(x->{
                        return x.get(1,Long.class);
                    })
                    .sum();


            int percent=StaticUtil.make_static_percent(sums,past_count);

            return ResponseStatic
                    .builder()
                    .currentStaticData(currentStataicData)
                    .currentCount(sums)
                    .pastCount(past_count)
                    .totCount(tot_count)
                    .percent(percent)
                    .build();

        }


        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER,"없는 형식의 파라미터 입니다");
    }



    public List<ResponseLatestData> getLatestData(RequestLatestData requestLatestData){

        Member admin=securityReadService.getMember();

        if(requestLatestData.getStaticDataType().name().equals(StaticDataType.Report.name())){

            List<ResponseLatestData> responseLatestDataList=queryFactory.select(
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
                                            .then(JPAExpressions.select(comment1.comment.substring(0,10))
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
                    .forEach(x->{
                        boolean readCheck=redisService.AdminReadCheck("ADMIN_ALARM",admin.getPublicId().toString()
                                ,x.getStaticDataType(),x.getContentId());
                        x.mappingCheckRead(readCheck);

                       if(x.getMainStatus().equals("SHOW")){
                           x.updateMainStatus("노출");
                       }
                        if(x.getMainStatus().equals("HIDDEN")){
                            x.updateMainStatus("숨김");
                        }
                        if(x.getMainStatus().equals("PENDING")){
                            x.updateMainStatus("보류");
                        }

                    });

            return responseLatestDataList;
        }

        if(requestLatestData.getStaticDataType().name().equals(StaticDataType.Inquiry.name())){


            List<ResponseLatestData> responseLatestDataList=queryFactory.select(
                            Projections.constructor(ResponseLatestData.class,
                                    Expressions.constant(""),
                                    Expressions.constant(StaticDataType.Inquiry),
                                    new CaseBuilder()
                                            .when(inquiry.isAdminAnswered.isTrue())
                                            .then("답변완료")
                                            .otherwise("답변대기"),
                                    new CaseBuilder()
                                            .when(member.email.isNotNull())
                                                .then("비회원")
                                                .otherwise("회원"),
                                    inquiry.id,
                                    new CaseBuilder()
                                                .when(member.email.isNotNull())
                                                .then(member.email)
                                                .otherwise(member.nickname),
                                    inquiry.content.substring(0,20),
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
                    .forEach(x->{
                        boolean readCheck=redisService.AdminReadCheck("ADMIN_ALARM",admin.getPublicId().toString()
                                ,x.getStaticDataType(),x.getContentId());
                        x.mappingCheckRead(readCheck);
                    });

            return responseLatestDataList;
        }if(requestLatestData.getStaticDataType().name().equals(StaticDataType.Improvement.name())){


            List<ResponseLatestData> responseLatestDataList=queryFactory.select(
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
                    .forEach(x->{
                        boolean readCheck=redisService.AdminReadCheck("ADMIN_ALARM",admin.getPublicId().toString()
                                ,x.getStaticDataType(),x.getContentId());
                        x.mappingCheckRead(readCheck);
                    });

            return responseLatestDataList;
        }

        throw new PlayHiveException(ErrorCode.INVALID_PARAMETER,"없는 형식의 파라미터 입니다");
    }



    private Predicate report_feat_report_type(ReportType reportType){
        return report.reportType.eq(reportType);
    }



}
