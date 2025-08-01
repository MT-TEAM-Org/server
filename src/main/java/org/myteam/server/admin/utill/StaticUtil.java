package org.myteam.server.admin.utill;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringTemplate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static org.myteam.server.admin.entity.QAdminContentChangeLog.adminContentChangeLog;
import static org.myteam.server.admin.entity.QAdminImproveChangeLog.adminImproveChangeLog;
import static org.myteam.server.admin.entity.QAdminInquiryChangeLog.*;
import static org.myteam.server.admin.entity.QAdminMemberChangeLog.adminMemberChangeLog;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.member.entity.QMemberAccess.memberAccess;
import static org.myteam.server.report.domain.QReport.report;

public class StaticUtil {

    public static Predicate betweenStaticTime(LocalDateTime static_end_time, LocalDateTime static_start_time, Object target) {
        Class<?> entityClass = ((EntityPath<?>) target).getType();
        String name = entityClass.getSimpleName();
        switch (name) {
            case "Report" -> {
                return report.createDate.goe(static_end_time).and(report.createDate.lt(static_start_time));
            }
            case "Member" -> {
                return member.createDate.goe(static_end_time).and(member.createDate.lt(static_start_time));
            }
            case "Board" -> {
                return board.createDate.goe(static_end_time).and(board.createDate.lt(static_start_time));
            }
            case "Comment" -> {
                return comment1.createDate.goe(static_end_time).and(comment1.createDate.lt(static_start_time));
            }
            case "Improvement" -> {
                return improvement.createDate.goe(static_end_time).and(improvement.createDate.lt(static_start_time));
            }
            case "Inquiry" -> {
                return inquiry.createdAt.goe(static_end_time).and(inquiry.createdAt.lt(static_start_time));
            }
            case "MemberAccess" -> {
                return memberAccess.accessTime.goe(static_end_time).and(memberAccess.accessTime.lt(static_start_time));
            }
            case "AdminMemberChangeLog" -> {
                return adminMemberChangeLog.createDate.goe(static_end_time).and(adminMemberChangeLog.createDate.lt(static_start_time));
            }
            case "AdminContentChangeLog" -> {
                return adminContentChangeLog.createDate.goe(static_end_time).and( adminContentChangeLog.createDate.lt(static_start_time));
            }
            case "AdminImproveChangeLog" -> {
                return adminImproveChangeLog.createDate.goe(static_end_time).and(adminImproveChangeLog.createDate.lt(static_start_time));
            }
            case "AdminInquiryChangeLog" -> {
                return adminInquiryChangeLog.createDate.goe(static_end_time).and(adminInquiryChangeLog.createDate.lt(static_start_time));
            }
            default -> {
                return null;
            }
        }
    }

    public static StringTemplate dateTemplate(DateType dateType, Object target) {
        Class<?> entityClass = ((EntityPath<?>) target).getType();
        String name = entityClass.getSimpleName();
        switch (name) {
            case "Report" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", report.createDate);
                }

                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", report.createDate);
            }
            case "Member" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", member.createDate);
                }
                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", member.createDate);
            }
            case "Board" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", board.createDate);
                }
                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", board.createDate);
            }
            case "Comment" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", comment1.createDate);
                }
                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", comment1.createDate);
            }
            case "Improvement" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", improvement.createDate);
                }
                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", improvement.createDate);
            }
            case "Inquiry" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", inquiry.createdAt);
                }
                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", inquiry.createdAt);
            }
            case "MemberAccess" -> {
                if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
                    return stringTemplate(
                            "DATE_FORMAT({0}, '%Y.%m.%d')", memberAccess.accessTime);
                }
                return stringTemplate("DATE_FORMAT({0}, '%Y.%m')", memberAccess.accessTime);
            }
            default -> {
                return null;
            }
        }
    }

    public static StringTemplate delTemplate(DateType dateType) {
        if (dateType.name().equals(DateType.Day.name()) || dateType.name().equals(DateType.WeekEnd.name())) {
            return stringTemplate(
                    "DATE_FORMAT({0}, '%Y-%m-%d')", member.deleteAt);
        }
        return stringTemplate("DATE_FORMAT({0}, '%Y-%m')", member.deleteAt);
    }

    public static Predicate betweenStaticTimeDel(LocalDateTime static_end_time, LocalDateTime static_start_time) {
        return member.deleteAt.goe(static_end_time).and(member.deleteAt.lt(static_start_time));
    }

    public static int makeStaticPercent(Long val1, Long val2) {
        if (val2 == 0) {
            return 100;
        }
        return Math.round(((float) (val1 - val2) / val2) * 100f);
    }

    public static String dateFormat(Date date, String mysqlFormatPattern) {
        if (date == null) {
            return null;
        }
        // MySQL 패턴을 Java SimpleDateFormat 패턴으로 변환
        String javaFormatPattern = mysqlFormatPattern
                .replace("%Y", "yyyy")
                .replace("%m", "MM")
                .replace("%d", "dd")
                .replace("%H", "HH") // 시 (00-23)
                .replace("%i", "mm") // 분 (00-59)
                .replace("%s", "ss"); // 초 (00-59)
        // 필요한 다른 MySQL 패턴도 여기에 추가 변환 로직을 넣을 수 있습니다.

        SimpleDateFormat sdf = new SimpleDateFormat(javaFormatPattern);
        return sdf.format(date);
    }


}
