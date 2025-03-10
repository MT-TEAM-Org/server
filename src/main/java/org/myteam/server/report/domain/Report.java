package org.myteam.server.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.chat.domain.BanReason;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "report",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_report", columnNames = {"reporter_id", "reported_id", "reportType", "reportedContentId"})
        }
)
public class Report extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false) // 신고한 사람
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false) // 신고된 사람
    private Member reported;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BanReason reason;

    private String reportIp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportType reportType; // 신고 유형 (게시글, 뉴스, 댓글 등)

    @Column(nullable = false)
    private Long reportedContentId; // 신고된 대상 ID (게시글 ID, 댓글 ID 등)

    @Builder
    public Report(Member reporter, Member reported, BanReason reason, String reportIp, ReportType reportType, Long reportedContentId) {
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.reportIp = reportIp;
        this.reportType = reportType;
        this.reportedContentId = reportedContentId;
    }

    public static Report createReport(Member reporter, Member reported, String reportIp,
                                      ReportType reportType, Long reportedContentId, BanReason reasons) {
        return Report.builder()
                .reporter(reporter)
                .reported(reported)
                .reportIp(reportIp)
                .reportType(reportType)
                .reportedContentId(reportedContentId)
                .reason(reasons)
                .build();
    }

}
