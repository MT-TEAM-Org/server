package org.myteam.server.report.repository;

import static org.myteam.server.report.domain.QReport.report;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.report.domain.QReportReason.reportReason;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BanReason;
import org.myteam.server.member.entity.QMember;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ReportSaveResponse> getAllReport(Pageable pageable) {

        List<ReportSaveResponse> content = queryFactory
                .select(Projections.constructor(ReportSaveResponse.class,
                        report.id.as("reportId"),
                        report.reporter.publicId.as("reporterPublicId"),
                        report.reported.publicId.as("reportedPublicId"),
                        report.reportType,
                        report.reportedContentId,
                        report.reason,
                        report.createDate,
                        report.reportIp
                ))
                .from(report)
                .join(member).on(member.eq(report.reporter))
                .join(member).on(member.eq(report.reported))
                .orderBy(report.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalReportCount();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<ReportSaveResponse> getReportList(ReportType reportType, Pageable pageable) {

        List<ReportSaveResponse> content = queryFactory
                .select(Projections.constructor(ReportSaveResponse.class,
                        report.id.as("reportId"),
                        report.reporter.publicId.as("reporterPublicId"),
                        report.reported.publicId.as("reportedPublicId"),
                        report.reportType,
                        report.reportedContentId,
                        report.reason,
                        report.createDate,
                        report.reportIp
                ))
                .from(report)
                .join(member).on(member.eq(report.reporter))
                .join(member).on(member.eq(report.reported))
                .where(report.reportType.eq(reportType))
                .orderBy(report.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalReportCountByType(reportType);

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 🚀 특정 사용자가 받은 신고 목록 조회 (페이징)
     */
    public Page<ReportSaveResponse> getReportsByReportedUser(UUID reportedPublicId, Pageable pageable) {
        QMember reporterMember = new QMember("reporterMember");
        QMember reportedMember = new QMember("reportedMember");

        List<ReportSaveResponse> content = queryFactory
                .select(Projections.constructor(ReportSaveResponse.class,
                        report.id,
                        report.reporter.publicId,
                        report.reported.publicId,
                        report.reportType,
                        report.reportedContentId,
                        report.reason,
                        report.createDate,
                        report.reportIp
                ))
                .from(report)
                .join(report.reporter, reporterMember)
                .join(report.reported, reportedMember)
                .where(report.reported.publicId.eq(reportedPublicId))
                .orderBy(report.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalReportCountByReportedUser(reportedPublicId);
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 🚀 특정 사용자가 보낸 신고 목록 조회 (페이징)
     */
    public Page<ReportSaveResponse> getReportsByReporter(UUID reporterPublicId, Pageable pageable) {
        QMember reporterMember = new QMember("reporterMember");
        QMember reportedMember = new QMember("reportedMember");

        List<ReportSaveResponse> content = queryFactory
                .select(Projections.constructor(ReportSaveResponse.class,
                        report.id,
                        report.reporter.publicId,
                        report.reported.publicId,
                        report.reportType,
                        report.reportedContentId,
                        report.reason,
                        report.createDate,
                        report.reportIp
                ))
                .from(report)
                .join(report.reporter, reporterMember)
                .join(report.reported, reportedMember)
                .where(report.reporter.publicId.eq(reporterPublicId))
                .orderBy(report.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalReportCountByReporter(reporterPublicId);
        return new PageImpl<>(content, pageable, total);
    }

    private long getTotalReportCount() {
        return queryFactory
                .select(report.count())
                .from(report)
                .fetchOne();
    }

    private long getTotalReportCountByType(ReportType reportType) {
        return queryFactory
                .select(report.count())
                .from(report)
                .where(report.reportType.eq(reportType))
                .fetchOne();
    }

    private long getTotalReportCountByReportedUser(UUID reportedPublicId) {
        return queryFactory
                .select(report.count())
                .from(report)
                .where(report.reported.publicId.eq(reportedPublicId))
                .fetchOne();
    }

    private long getTotalReportCountByReporter(UUID reporterPublicId) {
        return queryFactory
                .select(report.count())
                .from(report)
                .where(report.reporter.publicId.eq(reporterPublicId))
                .fetchOne();
    }
}
