package org.myteam.server.admin.utils;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import org.myteam.server.admin.entity.QUserAccessLog;
import org.myteam.server.board.domain.QBoard;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.improvement.domain.QImprovement;
import org.myteam.server.inquiry.domain.QInquiry;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.QMember;
import org.myteam.server.report.domain.ReportType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.myteam.server.admin.entity.QUserAccessLog.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.*;
import static org.myteam.server.improvement.domain.QImprovement.*;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.*;
import static org.myteam.server.report.domain.QReport.report;

@Component
public class StaticUtil {


    public static Predicate between_static_time(LocalDateTime static_end_time, LocalDateTime static_start_time,Object target){
        Class<?> entityClass = ((EntityPath<?>) target).getType();
        String name=entityClass.getSimpleName();

        switch(name){

            case "Report"->{return report.createDate.between(static_end_time,static_start_time);}
            case "Member"->{return member.createDate.between(static_end_time,static_start_time);}
            case "Board"->{return board.createDate.between(static_end_time,static_start_time);}
            case "Comment"->{return comment1.createDate.between(static_end_time,static_start_time);}
            case "Improvement"->{return improvement.createDate.between(static_end_time,static_start_time);}
            case "Inquiry"->{return inquiry.createdAt.between(static_end_time,static_start_time);}
            case "UserAccessLog"->{return userAccessLog.createDate.between(static_end_time,static_start_time);}
            default ->{ return null;}

        }
    }


    public static Predicate between_static_time_del(LocalDateTime static_end_time, LocalDateTime static_start_time){



        return member.deleteDate.between(static_end_time,static_start_time);

    }


    public static Predicate report_feat_report_type(ReportType reportType){

        return report.reportType.eq(reportType);
    }

    public static int make_static_percent(Long val1,Long val2){

      return  Math.round(((float)(val1-val2)/val2)*100f);

    }
}
