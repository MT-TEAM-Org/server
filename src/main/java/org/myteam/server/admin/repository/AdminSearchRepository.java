package org.myteam.server.admin.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.Union;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.processing.SQL;
import org.myteam.server.admin.dto.AdminDetail;
import org.myteam.server.admin.dto.AdminSearch;
import org.myteam.server.admin.utils.AdminControlType;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.repository.CommentQueryRepository;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.report.domain.ReportType;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.myteam.server.admin.dto.AdminSearch.*;
import static org.myteam.server.admin.utils.AdminControlType.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.report.domain.QReport.report;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AdminSearchRepository {


    private final JPAQueryFactory queryFactory;
    //union을 querydsl에서 쓰기 위해서 가져온것.
    private final JPASQLQuery<?> sqlQueryFactory;


    public Page<ResponseSearchContent> getCommentBoardList(RequestSearchContent requestSearchContent){

            if(requestSearchContent.getStaticDataType()==null){
                return getWhenDataTypeIsNull(requestSearchContent);
            }



            if(requestSearchContent.getStaticDataType().name().equals(StaticDataType.CommentData.name())){
                BoardSearchType boardSearchType=requestSearchContent.getBoardSearchType();
                String searchKeyWord=requestSearchContent.getSearchKeyWord();
                LocalDateTime startTime=requestSearchContent.getStartTime();
                LocalDateTime endTime=requestSearchContent.getEndTime();
            Long totNum=queryFactory.select(comment1.count())
                        .from(comment1)
                        .where(contentSearchTypeCond(requestSearchContent.getStaticDataType(),boardSearchType,searchKeyWord),
                                totContentCountCond(requestSearchContent.isReported(),requestSearchContent.getStaticDataType())
                                ,processStatusCond(requestSearchContent.getAdminControlType(),requestSearchContent.getStaticDataType())
                                ,searchByTimeLine(startTime, endTime,requestSearchContent.getStaticDataType()))
                        .fetch()
                        .get(0);

                Pageable pageable= PageRequest.of(requestSearchContent.getOffset(),10);

                List<ResponseSearchContent> responseSearchContents=queryFactory.select(Projections.constructor(ResponseSearchContent.class,
                                    comment1.id,
                                    member.nickname,
                                    Expressions.constant(StaticDataType.CommentData),
                                    comment1.comment.substring(0, 20),
                                    comment1.createDate,
                                    member.status,
                                    comment1.adminControlType,
                                    Expressions.constant(requestSearchContent.isReported())
                            ))
                            .from(comment1)
                            .leftJoin(comment1.member,member)
                            .where(contentSearchTypeCond(requestSearchContent.getStaticDataType(),boardSearchType,searchKeyWord),
                                    totContentCountCond(requestSearchContent.isReported(),requestSearchContent.getStaticDataType())
                                    ,processStatusCond(requestSearchContent.getAdminControlType(),requestSearchContent.getStaticDataType())
                                    ,searchByTimeLine(startTime, endTime,requestSearchContent.getStaticDataType()))
                            .orderBy(comment1.member.nickname.desc(),comment1.comment.desc()
                                    ,comment1.createDate.desc(),
                                    comment1.member.status.desc(),
                                    comment1.adminControlType.desc())
                            .offset(pageable.getOffset())
                            .limit(pageable.getPageSize())
                            .fetch();

                    return new PageImpl<>(responseSearchContents,pageable,totNum);



            }

/*        if(requestSearchContent.getBoardSearchType().name().equals(StaticDataType.BoardData.name())){



        }*/



        return getAdminBoardList(requestSearchContent);


    }

    public Page<ResponseSearchContent> getWhenDataTypeIsNull(RequestSearchContent requestSearchContent){

        BoardSearchType boardSearchType=requestSearchContent.getBoardSearchType();
        String searchKeyWord=requestSearchContent.getSearchKeyWord();
        LocalDateTime startTime=requestSearchContent.getStartTime();
        LocalDateTime endTime=requestSearchContent.getEndTime();



        Pageable pageable= PageRequest.of(requestSearchContent.getOffset(),10);




        List<ResponseSearchContent> aboutComment=queryFactory.select(Projections.constructor(ResponseSearchContent.class,
                        comment1.id,
                        member.nickname,
                        Expressions.constant(StaticDataType.CommentData),
                        comment1.comment.substring(0, 20),
                        comment1.createDate,
                        member.status,
                        comment1.adminControlType,
                        Expressions.constant(requestSearchContent.isReported())
                ))
                .from(comment1)
                .join(member)
                .on(member.eq(comment1.member))
                .where(contentSearchTypeCond(StaticDataType.CommentData,boardSearchType,searchKeyWord),
                        totContentCountCond(requestSearchContent.isReported(),StaticDataType.CommentData)
                        ,processStatusCond(requestSearchContent.getAdminControlType(),StaticDataType.CommentData)
                        ,searchByTimeLine(startTime, endTime,StaticDataType.CommentData))
                .fetch();

        List<ResponseSearchContent> aboutBoard=queryFactory.select(Projections.constructor(ResponseSearchContent.class,
                        board.id,
                        member.nickname,
                        Expressions.constant(StaticDataType.BoardData), // .name()으로 String 변환
                        board.title, // 게시글 내용은 title을 그대로 사용한다고 가정
                        board.createDate,
                        member.status,
                        board.adminControlType,
                        Expressions.constant(requestSearchContent.isReported()))
                )
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .where(contentSearchTypeCond(StaticDataType.BoardData,boardSearchType, searchKeyWord),
                        searchByTimeLine(startTime, endTime,StaticDataType.CommentData),
                        totContentCountCond(requestSearchContent.isReported(),StaticDataType.BoardData),
                        processStatusCond(requestSearchContent.getAdminControlType(),StaticDataType.BoardData))
                .fetch();

        List<ResponseSearchContent> merged=new ArrayList<>();
        merged.addAll(aboutBoard);
        merged.addAll(aboutComment);

        List<ResponseSearchContent> responseSearchContents = merged.stream()
                .sorted(Comparator.comparing(ResponseSearchContent::getCreateTime).reversed())
                .toList();
        List<ResponseSearchContent> last;

        if(responseSearchContents.isEmpty()){

            last=List.of();
        } else if((int)pageable.getOffset()*10+10>responseSearchContents.size()){

            last=responseSearchContents.subList((int)pageable.getOffset()*10,responseSearchContents.size());

        }


        else{
            last=responseSearchContents.subList((int)pageable.getOffset()*10,(int)pageable.getOffset()*10+10);
        }







        Long totNum=queryFactory.select(comment1.count())
                .from(comment1)
                .where(contentSearchTypeCond(StaticDataType.CommentData,boardSearchType,searchKeyWord),
                        totContentCountCond(requestSearchContent.isReported(),StaticDataType.CommentData)
                        ,processStatusCond(requestSearchContent.getAdminControlType(),StaticDataType.CommentData)
                        ,searchByTimeLine(startTime, endTime,StaticDataType.CommentData))
                .fetch()
                .get(0);
        Long totNum2=queryFactory.
                select(board.count())
                .from(board)
                .where(contentSearchTypeCond(StaticDataType.BoardData,boardSearchType, searchKeyWord),
                        searchByTimeLine(startTime, endTime,StaticDataType.BoardData),
                        totContentCountCond(requestSearchContent.isReported(),StaticDataType.BoardData),
                        processStatusCond(requestSearchContent.getAdminControlType(),StaticDataType.BoardData))
                .fetch().get(0);


        return new PageImpl<>(last,pageable,totNum2+totNum);

    }

    public Page<ResponseSearchContent> getAdminBoardList(RequestSearchContent requestSearchContent){
        BoardSearchType boardSearchType=requestSearchContent.getBoardSearchType();
        String searchKeyWord=requestSearchContent.getSearchKeyWord();
        LocalDateTime startTime=requestSearchContent.getStartTime();
        LocalDateTime endTime=requestSearchContent.getEndTime();

        Long totNum=queryFactory.
                select(board.count())
                .from(board)
                .where(contentSearchTypeCond(requestSearchContent.getStaticDataType(),boardSearchType, searchKeyWord),
                        searchByTimeLine(startTime, endTime,requestSearchContent.getStaticDataType()),
                        totContentCountCond(requestSearchContent.isReported(),requestSearchContent.getStaticDataType()),
                        processStatusCond(requestSearchContent.getAdminControlType(),requestSearchContent.getStaticDataType()))
                .fetch().get(0);

        Pageable pageable= PageRequest.of(requestSearchContent.getOffset(),10);
        List<ResponseSearchContent> responseSearchContents=queryFactory.select(Projections.constructor(ResponseSearchContent.class,
                        board.id,
                        member.nickname,
                        Expressions.constant(StaticDataType.BoardData),
                        board.title,
                        board.createDate,
                        member.status,
                        board.adminControlType,
                        Expressions.constant(requestSearchContent.isReported())
                ))
                .from(board)
                .join(member)
                .on(member.eq(board.member))
                .where(contentSearchTypeCond(requestSearchContent.getStaticDataType(),boardSearchType, searchKeyWord),
                        searchByTimeLine(startTime, endTime,requestSearchContent.getStaticDataType()),
                        totContentCountCond(requestSearchContent.isReported(),requestSearchContent.getStaticDataType()),
                        processStatusCond(requestSearchContent.getAdminControlType(),requestSearchContent.getStaticDataType()))
                .orderBy(board.member.nickname.desc(),board.title.desc()
                        ,board.createDate.desc(),
                        board.member.status.desc(),
                        board.adminControlType.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(responseSearchContents,pageable,totNum);
    }

    public Page<ResponseSearchInquiryImprovement> getInquiryImprovementList(RequestSearchInquiryImprovement requestSearchInquiryImprovement){

        StaticDataType staticDataType=requestSearchInquiryImprovement.getStaticDataType();
        int offset= requestSearchInquiryImprovement.getOffset();

        if(staticDataType.name().equals(StaticDataType.InquiryData.name())) {


            Pageable pageable= PageRequest.of(offset,10);
            List<ResponseSearchInquiryImprovement> responseSearchInquiries=queryFactory.select(Projections.constructor(ResponseSearchInquiryImprovement.class
                                ,new CaseBuilder()
                                        .when(inquiry.isAdminAnswered.isTrue())
                                        .then(Expressions.constant(ImprovementStatus.RECEIVED.name()))
                                        .otherwise(Expressions.constant(ImprovementStatus.PENDING.name())),
                                inquiry.id,
                                new CaseBuilder()
                                        .when(member.nickname.isNotEmpty())
                                        .then(member.nickname)
                                        .otherwise(member.email),
                                inquiry.content.substring(0,20),
                                inquiry.createdAt
                        ))
                        .from(inquiry)
                        .join(member)
                        .on(member.eq(inquiry.member))
                        .offset(pageable.getOffset())
                        .orderBy(inquiry.createdAt.desc())
                        .limit(pageable.getPageSize())
                        .fetch();
                Long count=queryFactory.select(inquiry.count())
                        .from(inquiry)
                        .fetch().get(0);

                return new PageImpl<>(responseSearchInquiries,pageable,count);


        }



        Pageable pageable= PageRequest.of(offset,10);
            List<ResponseSearchInquiryImprovement> responseSearchInquiries=queryFactory.select(Projections.constructor(ResponseSearchInquiryImprovement.class
                            ,Expressions.constant(improvement.improvementStatus.toString()),
                            improvement.id,
                            new CaseBuilder().when(member.nickname.isNotEmpty())
                                    .then(member.nickname)
                                    .otherwise(member.email),
                            improvement.title,
                            improvement.createDate
                    ))
                    .from(improvement)
                    .join(member)
                    .on(member.eq(improvement.member))
                    .offset(pageable.getOffset())
                    .orderBy(improvement.createDate.desc())
                    .limit(pageable.getPageSize())
                    .fetch();
            Long count=queryFactory.select(improvement.count())
                    .from(improvement)
                    .fetch().get(0);

            return new PageImpl<>(responseSearchInquiries,pageable,count);




    }

    public Page<ResponseSearchUserList> getUserInfoList(RequestSearchUserList requestSearchUserList){

            Pageable pageable=PageRequest.of(requestSearchUserList.getOffset(),10);


            List<ResponseSearchUserList> responseSearchUserLists=queryFactory.select(Projections.constructor(ResponseSearchUserList.class,
                            Expressions.constant(member.publicId.toString()),
                            member.nickname,
                            member.email,
                            member.tel,
                            member.genderType,
                            member.birthYear,
                            member.birthMonth,
                            member.birthDay,
                            member.createDate,
                            member.status
                            ))
                    .from(member)
                    .where(userListSearchCond(requestSearchUserList))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            Long userTot=queryFactory.select(member.count())
                    .from(member)
                    .where(userListSearchCond(requestSearchUserList))
                    .fetch()
                    .get(0);
            return new PageImpl<>(responseSearchUserLists,pageable,userTot);
    }

    public Page<ResponseUserReportedList> getUserReportedList(RequestUserReportedList requestUserReportedList){
        Pageable pageable=PageRequest.of(requestUserReportedList.getOffset(),10);
        UUID uuid=requestUserReportedList.getUuid();
        List<ResponseUserReportedList> responseUserReportedLists=queryFactory.select(Projections.constructor(ResponseUserReportedList.class,
                report.id,
                report.reportType,
                new CaseBuilder()
                        .when(report.reportType.eq(ReportType.COMMENT))
                        .then(JPAExpressions.select(comment1.comment.substring(0,20))
                                .from(comment1)
                                .where(report.reportedContentId.eq(comment1.id))
                                )
                        //서브 쿼리문에서 최종실행인 fetch는 x
                        .otherwise(JPAExpressions.select(board.title)
                                .from(board)
                                .where(report.reportedContentId.eq(board.id))),
                report.reason,
                report.createDate
                ))
                .from(report)
                .join(member)
                .on(member.eq(report.reported))
                .where(member.publicId.eq(uuid))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(report.createDate.desc())
                .fetch();
        Long totCount=queryFactory.select(report.count())
                .from(report)
                .join(member)
                .on(member.eq(report.reported))
                .where(member.publicId.eq(uuid))
                .fetch()
                .get(0);

        return new PageImpl<>(responseUserReportedLists,pageable,totCount);
    }



        private Predicate contentSearchTypeCond(StaticDataType staticDataType,BoardSearchType boardSearchType, String searchKeyWord){


            if(boardSearchType==null ||searchKeyWord==null){
                log.info("키워드가 null입니다{}",searchKeyWord==null);
                return null;
            }

            if(staticDataType.name().equals(StaticDataType.CommentData.name())){



            switch (boardSearchType){

                case NICKNAME -> {return member.nickname.like("%"+searchKeyWord+"%");}
                case TITLE-> {return null;}
                case COMMENT,TITLE_CONTENT,CONTENT-> {return comment1.comment.like("%"+searchKeyWord+"%");}


            }}

            switch (boardSearchType){

                case NICKNAME -> {return member.nickname.like("%"+searchKeyWord+"%");}
                case TITLE-> {return board.title.like("%"+searchKeyWord+"%");}
                case TITLE_CONTENT,CONTENT-> {return board.title.contains(searchKeyWord).or(board.content.like("%"+searchKeyWord+"%"));}
                default -> {return null;}
            }

        }


    private Predicate processStatusCond(AdminControlType adminControlType,StaticDataType staticDataType){

        if(adminControlType==null){
            log.info("error체크") ;
            return null;
        }

        if(staticDataType.name().equals(StaticDataType.CommentData.name())) {
            switch (adminControlType) {

                case NORMAL -> {
                    return comment1.adminControlType.eq(NORMAL);
                }

                case INIT -> {
                    return comment1.adminControlType.eq(INIT);
                }
                case PROCESSING -> {
                    return comment1.adminControlType.eq(PROCESSING);
                }
                case HIDDEN -> {
                    return comment1.adminControlType.eq(HIDDEN);
                }
            }
        }


        switch (adminControlType) {

            case NORMAL -> {
                return board.adminControlType.eq(NORMAL);
            }

            case INIT -> {
                return board.adminControlType.eq(INIT);
            }
            case PROCESSING -> {
                return board.adminControlType.eq(PROCESSING);
            }
            case HIDDEN -> {
                return board.adminControlType.eq(HIDDEN);
            }
        }

        return null;

    }


    private Predicate searchByTimeLine(LocalDateTime startTime,LocalDateTime endTime,StaticDataType staticDataType){

        if(staticDataType.name().equals(StaticDataType.BoardData.name())){
            if(startTime==null & endTime==null){

                return null;
            }
            if(startTime!=null &endTime==null){

                return board.createDate.after(startTime);
            }
            if(startTime == null){

                return board.createDate.before(endTime);
            }

            return board.createDate.between(startTime,endTime);
        }

        if (startTime != null & endTime == null) {

            return comment1.createDate.after(startTime);
        }
        if (endTime != null & startTime == null) {

                return comment1.createDate.before(endTime);
        }

        if(startTime==null & endTime==null){

            return null;
        }
        return comment1.createDate.between(startTime,endTime);



    }


    private Predicate totContentCountCond(boolean reported,StaticDataType staticDataType){



        if(staticDataType.name().equals(StaticDataType.CommentData.name())){
            if (reported) {
                return comment1.id.in(
                        JPAExpressions.select(report.reportedContentId)
                                .from(report)
                                .where(report.reportType.eq(ReportType.COMMENT)));
            }
            return comment1.id.notIn(
                    JPAExpressions.select(report.reportedContentId)
                            .from(report)
                            .where(report.reportType.eq(ReportType.COMMENT)));


        }

        if(reported){
            return  board.id.in(
                    JPAExpressions.select(report.reportedContentId)
                            .from(report)
                            .where(report.reportType.eq(ReportType.BOARD)));
        }

        return board.id.notIn(
                JPAExpressions.select(report.reportedContentId)
                        .from(report)
                        .where(report.reportType.eq(ReportType.BOARD)));


    }



    private BooleanBuilder userListSearchCond(RequestSearchUserList requestSearchUserList){
            BooleanBuilder booleanBuilder=new BooleanBuilder();

            if(requestSearchUserList.getNickName()!=null){

                booleanBuilder.and(member.nickname.like("%"+requestSearchUserList.getNickName()+"%"));
            }
            if(requestSearchUserList.getEmail()!=null){

                booleanBuilder.and(member.email.eq(requestSearchUserList.getEmail()));
            }
            if(requestSearchUserList.getPhoneNumber()!=null){

                booleanBuilder.and(member.tel.eq(requestSearchUserList.getPhoneNumber()));
            }
            if(requestSearchUserList.getStartTime()!=null){

                booleanBuilder.and(member.createDate.after(requestSearchUserList.getStartTime()));
            }
            if(requestSearchUserList.getEndTime()!=null){

                booleanBuilder.and(member.createDate.before(requestSearchUserList.getEndTime()));
            }
            if(requestSearchUserList.getMemberType()!=null){
                booleanBuilder.and(member.type.eq(requestSearchUserList.getMemberType()));
            }

            List<Integer> brithInfo=requestSearchUserList.provideBirthday();
            IntStream.range(0,3)
                            .forEach(x->{

                                if(x==0){
                                    if (brithInfo.get(0)>0){
                                        booleanBuilder.and(member.birthYear.eq(brithInfo.get(x)));
                                    }
                                    if (brithInfo.get(1)>0){
                                        booleanBuilder.and(member.birthMonth.eq(brithInfo.get(x)));
                                    }
                                    if(brithInfo.get(2)>0){
                                        booleanBuilder.and(member.birthDay.eq(brithInfo.get(x)));
                                    }

                                }

                            });


        return booleanBuilder;
    }
    public Page<ResponseReportList> getReportList(RequestReportList requestReportList){
        Pageable pageable= PageRequest.of(requestReportList.getOffset(),10);





        if (requestReportList.getStaticDataType().name().equals(StaticDataType.CommentData.name())) {

            Long tot=queryFactory.select(report.count())
                    .from(report)
                    .where(report.reportType.eq(ReportType.COMMENT),report.reportedContentId.eq(requestReportList.getId()))
                    .fetch().get(0);



            List<ResponseReportList> responseReportLists= queryFactory.select(Projections.constructor(ResponseReportList.class,
                            Expressions.constant(requestReportList.getStaticDataType()),
                            report.id,
                            comment1.comment.substring(0,20),
                            report.reason,
                            report.createDate


                    ))
                    .from(report)
                    .join(comment1)
                    .on(comment1.id.eq(report.reportedContentId))
                    .where(report.reportType.eq(ReportType.COMMENT),report.reportedContentId.eq(requestReportList.getId()))
                    .orderBy(report.createDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();


            return new PageImpl<>(responseReportLists,pageable,tot);

        }
        Long tot=queryFactory.select(report.count())
                .from(report)
                .where(report.reportType.eq(ReportType.BOARD),report.reportedContentId.eq(requestReportList.getId()))
                .fetch().get(0);




        List<ResponseReportList> responseReportLists= queryFactory.select(Projections.constructor(ResponseReportList.class,
                        Expressions.constant(requestReportList.getStaticDataType()),
                        report.id,
                        board.title,
                        report.reason,
                        report.createDate
                ))
                .from(report)
                .join(board)
                .on(board.id.eq(report.reportedContentId))
                .where(report.reportType.eq(ReportType.BOARD),report.reportedContentId.eq(requestReportList.getId()))
                .orderBy(report.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(responseReportLists,pageable,tot);

    }
}
