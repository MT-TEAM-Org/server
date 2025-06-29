package org.myteam.server.admin.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.MemberMemo;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.report.domain.QReportReason;
import org.myteam.server.report.domain.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import static org.myteam.server.admin.dto.AdminDetail.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.report.domain.QReport.report;
import static org.myteam.server.report.domain.QReportReason.*;

@Repository
@RequiredArgsConstructor
public class AdminDetailRepository {

    private final JPAQueryFactory queryFactory;

    private final MemberReadService memberReadService;

    private final MemberMemoRepository memberMemoRepository;



    public ResponseContentDetail getCommentBoardDetail(RequestContentDetail requestContentDetail){



            return getContentDetail(requestContentDetail);


    }

    public ResponseClient getRequestClientDetail(RequestClient requestClient){


        if(requestClient.getStaticDataType().name().equals(StaticDataType.InquiryData.name())){

            return queryFactory.select(Projections.constructor(ResponseClient.class,
                                new CaseBuilder()
                                        //casebuilder로 넘길떄에는 enujm을 넘길수가없다.
                                        //그냥 넘길떈 가능
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then(ImprovementStatus.RECEIVED.name())
                                        .otherwise(ImprovementStatus.PENDING.name())
                                ,new CaseBuilder()
                                        .when(member.nickname.isNotEmpty())
                                        .then(member.nickname)
                                        .otherwise(member.email),
                                Expressions.constant(""),
                                inquiry.createdAt,
                                inquiry.content
                        ))
                        .from(inquiry)
                        .join(member)
                        .on(member.eq(inquiry.member))
                        .where(inquiry.id.eq(requestClient.getId()))
                        .fetch().get(0);
        }

      return queryFactory.select(Projections.constructor(ResponseClient.class,
                        new CaseBuilder()
                                .when(improvement.improvementStatus.eq(ImprovementStatus.PENDING))
                                .then(ImprovementStatus.PENDING.name())
                                .when(improvement.improvementStatus.eq(ImprovementStatus.RECEIVED))
                                .then(ImprovementStatus.RECEIVED.name())
                                .otherwise(ImprovementStatus.COMPLETED.name())
                        ,new CaseBuilder()
                                .when(member.nickname.isNotEmpty())
                                .then(member.nickname)
                                .otherwise(member.email),
                        improvement.title,
                        improvement.createDate,
                        improvement.content

                ))
                .from(improvement)
                .join(member)
                .on(member.eq(improvement.member))
                .where(improvement.id.eq(requestClient.getId()))
                .fetch().get(0);

    }

    public ResponseUserDetail getUserDetail(RequestUserDetail requestUserDetail){

        Member member1=memberReadService.findById(requestUserDetail.getUuid());

        Optional<MemberMemo> memberMemo=memberMemoRepository.findByReportedId(requestUserDetail.getUuid());

        if(memberMemo.isEmpty()){
            memberMemoRepository.save(MemberMemo.builder()
                    .uuid(requestUserDetail.getUuid())
                    .content("")
                    .build());
            return ResponseUserDetail.builder()
                    .userId(member1.getPublicId().toString())
                    .email(member1.getEmail())
                    .nickName(member1.getNickname())
                    .genderType(member1.getGenderType())
                    .createAt(member1.getCreateDate())
                    .memberStatus(member1.getStatus())
                    .birthYear(member1.getBirthYear())
                    .birthMonth(member1.getBirthMonth())
                    .birthDay(member1.getBirthDay())
                    .memberType(member1.getType())
                    .tel(member1.getTel())
                    .content("")
                    .build();
        }

        return ResponseUserDetail.builder()
                .userId(member1.getPublicId().toString())
                .email(member1.getEmail())
                .nickName(member1.getNickname())
                .genderType(member1.getGenderType())
                .createAt(member1.getCreateDate())
                .memberStatus(member1.getStatus())
                .birthYear(member1.getBirthYear())
                .birthMonth(member1.getBirthMonth())
                .birthDay(member1.getBirthDay())
                .memberType(member1.getType())
                .tel(member1.getTel())
                .content(memberMemo.get().getContent())
                .build();




    }

    public ResponseUserReportDetail getUserReportedDetail(RequestUserReportDetail req){

        ResponseUserReportDetail responseUserReportDetail = queryFactory.select(Projections.constructor(ResponseUserReportDetail.class,
                        report.id,
                        report.reportType,
                        new CaseBuilder()
                                .when(report.reportType.eq(ReportType.COMMENT))
                                .then(JPAExpressions.select(comment1.comment.substring(0, 20))
                                        .from(comment1)
                                        .where(comment1.id.eq(report.reportedContentId)))
                                .otherwise(JPAExpressions.select(board.title)
                                        .from(board)
                                        .where(board.id.eq(report.reportedContentId))),

                        report.reason,
                       // reportReason.reportedReason,
                        Expressions.constant(""),
                        report.createDate
                ))
                .from(report)
                .where(report.id.eq(req.getReportId()))
                /*.join(reportReason)
                .on(reportReason.report.eq(report))*/
                .fetch()
                .get(0);
        return responseUserReportDetail;
    }



    private ResponseContentDetail getContentDetail(RequestContentDetail requestContentDetail){

        if (requestContentDetail.getStaticDataType()
                .name().equals(StaticDataType.CommentData.name())){

            return queryFactory.select(Projections.constructor(ResponseContentDetail.class,
                                comment1.adminControlType,
                                member.nickname,
                                Expressions.constant(StaticDataType.CommentData),
                                comment1.comment.substring(0,20),
                                comment1.createDate,
                                member.status,
                                new CaseBuilder()
                                        .when(JPAExpressions.select(report.reportedContentId).from(report)
                                                .where(report.reportedContentId.eq(requestContentDetail.getId())
                                                        ,report.reportType.eq(ReportType.COMMENT)).isNotNull())
                                        .then(true)
                                        .otherwise(false)))
                        .from(comment1)
                        .join(member)
                        .on(member.eq(comment1.member))
                        .fetch().get(0);


        }

        return queryFactory.select(Projections.constructor(ResponseContentDetail.class,
                        board.adminControlType,
                        member.nickname,
                        Expressions.constant(StaticDataType.BoardData),
                        board.title,
                        board.createDate,
                        member.status,
                        new CaseBuilder().when(JPAExpressions.select(report.reportedContentId).from(report).
                                        where(report.reportedContentId.eq(requestContentDetail.getId()),report.reportType.eq(ReportType.BOARD)).isNotNull())
                                .then(true)
                                .otherwise(false)))
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .fetch().get(0);

    }




}
