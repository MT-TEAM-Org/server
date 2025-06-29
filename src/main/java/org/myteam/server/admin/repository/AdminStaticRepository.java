package org.myteam.server.admin.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.entity.QUserAccessLog;
import org.myteam.server.admin.utils.StaticDataType;

import org.myteam.server.admin.utils.StaticUtil;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.inquiry.domain.QInquiry;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.repository.ReportQueryRepository;
import org.myteam.server.report.service.ReportReadService;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static org.myteam.server.admin.dto.AdminStatic.*;
import static org.myteam.server.admin.entity.QUserAccessLog.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.*;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.report.domain.QReport.report;


@Repository
@RequiredArgsConstructor
@Slf4j
public class AdminStaticRepository {

    private final JPAQueryFactory queryFactory;

    public List<Long> getStaticData(StaticDataType staticDataType, List<LocalDateTime> static_time_list){


       return MakeStaticData(staticDataType,static_time_list);


    }


    public List<LatestData> getLatestReportList(StaticDataType staticDataType){

        return MakeLatestData(staticDataType);

    }





    private List<Long> MakeStaticData(StaticDataType staticDataType,List<LocalDateTime> static_time_list){

        LocalDateTime static_start_time=static_time_list.get(0);
        LocalDateTime static_end_time=static_time_list.get(1);
        LocalDateTime static_start_time2=static_time_list.get(2);
        LocalDateTime static_end_time2=static_time_list.get(3);

        if(staticDataType.name().equals(StaticDataType.ReportedBoardData.name())
                || staticDataType.name().equals(StaticDataType.ReportedCommentData.name())){


            ReportType reportType;
            reportType=ReportType.COMMENT;
            if(staticDataType.name().equals(StaticDataType.ReportedBoardData.name())){
                reportType=ReportType.BOARD;
            }


            Long static_count_now=queryFactory.select(report.count())
                        .from(report)
                        .where(StaticUtil.between_static_time(static_end_time,static_start_time,report),StaticUtil.report_feat_report_type(reportType))
                        .fetch()
                        .get(0);
                Long static_count_past=queryFactory.select(report.count())
                        .from(report)
                        .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,report),StaticUtil.report_feat_report_type(reportType))
                        .fetch()
                        .get(0);

                Long static_count_tot=getTotalReportCountByType(reportType);

                return List.of(static_count_now,static_count_past,static_count_tot);

        }

        if(staticDataType.name().equals(StaticDataType.UserSingInData.name())){

                Long current_count=queryFactory.select(member.count())
                        .from(member)
                        .where(StaticUtil.between_static_time(static_end_time,static_start_time,member))
                        .fetch().get(0);
                Long past_count=queryFactory.select(member.count())
                        .from(member)
                        .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,member))
                        .fetch().get(0);

                Long tot_count=queryFactory.select(member.count())
                        .from(member)
                        .fetch().get(0);


                return List.of(current_count,past_count,tot_count);

        }
        if(staticDataType.name().equals(StaticDataType.BoardData.name())){

            Long static_count_now=queryFactory.select(board.count())
                    .from(board)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,board))
                    .fetch()
                    .get(0);
            Long static_count_past=queryFactory.select(board.count())
                    .from(board)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,board))
                    .fetch()
                    .get(0);

            Long static_count_tot=queryFactory.select(board.count())
                    .from(board)
                    .fetch()
                    .get(0);

            return List.of(static_count_now,static_count_past,static_count_tot);


        }
        if( staticDataType.name().equals(StaticDataType.CommentData.name())){


            Long static_count_now=queryFactory.select(comment1.count())
                    .from(comment1)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,comment1))
                    .fetch()
                    .get(0);
            Long static_count_past=queryFactory.select(comment1.count())
                    .from(comment1)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,comment1))
                    .fetch()
                    .get(0);

            Long static_count_tot=queryFactory.select(comment1.count())
                    .from(comment1)
                    .fetch()
                    .get(0);

            return List.of(static_count_now,static_count_past,static_count_tot);
        }
        if(staticDataType.name().equals(StaticDataType.ImprovementData.name())){

            Long static_count_now=queryFactory.select(improvement.count())
                    .from(improvement)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,improvement))
                    .fetch()
                    .get(0);
            Long static_count_past=queryFactory.select(improvement.count())
                    .from(improvement)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,improvement))
                    .fetch()
                    .get(0);

            Long static_count_tot=getTotalImprovementCount();

            return List.of(static_count_now,static_count_past,static_count_tot);

        }
        if(staticDataType.name().equals(StaticDataType.UserDeletedData.name())){
            Long current_count=queryFactory.select(member.count()).
                    from(member)
                    .where(StaticUtil.between_static_time_del(static_end_time,static_start_time))
                    .fetch().get(0);
            Long past_count=queryFactory.select(member.count()).
                    from(member)
                    .where(StaticUtil.between_static_time_del(static_end_time2,static_start_time2))
                    .fetch().get(0);

            Long tot_count=queryFactory.select(member.count())
                    .from(member)
                    .where(member.deleteDate.isNotNull())
                    .fetch().get(0);
            log.info("now_log:{}",current_count);
            log.info("past_log:{}",past_count);
            log.info("delete_log:{}",tot_count);
            return List.of(current_count,past_count,tot_count);

        }

        if(staticDataType.name().equals(StaticDataType.UserAccessData.name())){

            Long current_count=queryFactory.select(userAccessLog.count()).
                    from(userAccessLog)
                    .where(StaticUtil.between_static_time(static_end_time,static_start_time,userAccessLog))
                    .fetch().get(0);
            Long past_count=queryFactory.select(userAccessLog.count()).
                    from(userAccessLog)
                    .where(StaticUtil.between_static_time(static_end_time2,static_start_time2,userAccessLog))
                    .fetch().get(0);

            Long tot_count=queryFactory.select(userAccessLog.count())
                    .from(userAccessLog)
                    .fetch().get(0);
            return List.of(current_count,past_count,tot_count);


        }


        return null;



    }



    private List<LatestData> MakeLatestData(StaticDataType staticDataType){
        if(staticDataType.name().equals(StaticDataType.Report.name())){

            return queryFactory.select(Projections.constructor(LatestData.class,
                            new CaseBuilder()
                                    .when(report.reportType.eq(ReportType.COMMENT))
                                    .then(StaticDataType.ReportedCommentData)
                                    .when(report.reportType.eq(ReportType.BOARD))
                                    .then(StaticDataType.ReportedBoardData)
                                    .otherwise(StaticDataType.ReportedChat),
                            report.reportedContentId,
                            new CaseBuilder()
                                    .when(report.reportType.eq(ReportType.COMMENT))
                                    .then(JPAExpressions.select(comment1.comment.substring(0,20)).from(comment1)
                                            .where(comment1.id.eq(report.reportedContentId)))
                                    .otherwise(JPAExpressions.select(board.title)
                                            .from(board)
                                            .where(board.id.eq(report.reportedContentId)))
                            ,new CaseBuilder().when(member.nickname.isNotEmpty())
                                    .then(member.nickname)
                                    .otherwise(member.email)
                            ,new CaseBuilder()
                                    .when(report.reportType.eq(ReportType.COMMENT))
                                    .then(JPAExpressions.select(comment1.createDate).from(comment1)
                                            .where(comment1.id.eq(report.reportedContentId)))
                                    .otherwise(JPAExpressions.select(board.createDate)
                                            .from(board)
                                            .where(board.id.eq(report.reportedContentId)))
                    ))
                    .from(report)
                    .join(member)
                    .on(member.eq(report.reported))
                    .orderBy(report.createDate.desc())
                    .limit(10)
                    .fetch();



        }
        if(staticDataType.name().equals(StaticDataType.BoardData.name())
                ||staticDataType.name().equals(StaticDataType.CommentData.name())){
            List<LatestData> latestData=queryFactory.select(
                                Projections.constructor(LatestData.class,
                                        Expressions.constant(StaticDataType.BoardData),
                                        board.id,
                                        board.title,
                                        member.nickname,
                                        board.createDate
                                )
                        )
                        .from(board)
                        .join(member)
                        .on(member.eq(board.member))
                        .limit(5)
                        .orderBy(board.createDate.desc())
                        .fetch();


            List<LatestData> latestData2=queryFactory.select(
                            Projections.constructor(LatestData.class,
                                    Expressions.constant(StaticDataType.CommentData),
                                    comment1.id,
                                    comment1.comment.substring(0,20),
                                    member.nickname,
                                    comment1.createDate
                            )
                    )
                    .from(comment1)
                    .join(member)
                    .on(member.eq(comment1.member))
                    .limit(5)
                    .orderBy(comment1.createDate.desc())
                    .fetch();

            List<LatestData> mergedList = new ArrayList<>();
            mergedList.addAll(latestData);
            mergedList.addAll(latestData2);

            mergedList.sort(Comparator.comparing(LatestData::getCreatedAt).reversed());


            return mergedList;
        }
        List<LatestData> lst=queryFactory.select(Projections.constructor(LatestData.class,
                        Expressions.constant(StaticDataType.ImprovementData),
                        improvement.id,
                        improvement.title,
                        new CaseBuilder().when(member.nickname.isNotEmpty())
                                .then(member.nickname)
                                .otherwise(member.email),
                        improvement.createDate
                ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .limit(5)
                .orderBy(improvement.createDate.desc())
                .fetch();
        List<LatestData> lst2=queryFactory.select(Projections.constructor(LatestData.class,
                        Expressions.constant(StaticDataType.InquiryData),
                        inquiry.id,
                        inquiry.content.substring(0,20),
                        new CaseBuilder().when(member.nickname.isNotEmpty())
                                .then(member.nickname)
                                .otherwise(member.email),
                        inquiry.createdAt
                ))
                .from(inquiry)
                .join(member)
                .on(member.eq(inquiry.member))
                .limit(5)
                .orderBy(inquiry.createdAt.desc())
                .fetch();

        List<LatestData> mergedList = new ArrayList<>();
        mergedList.addAll(lst);
        mergedList.addAll(lst2);

        mergedList.sort(Comparator.comparing(LatestData::getCreatedAt).reversed());


        return mergedList;

    }

    private long getTotalReportCountByType(ReportType reportType) {
        return queryFactory
                .select(report.count())
                .from(report)
                .where(report.reportType.eq(reportType))
                .fetch().get(0);
    }

    private Long getTotalImprovementCount(){
        return queryFactory.select(improvement.count())
                .from(improvement)
                .fetch().get(0);
    }




}
