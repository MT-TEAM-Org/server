package org.myteam.server.report.domain;

import jakarta.persistence.*;
import lombok.*;
import org.myteam.server.chat.block.domain.BanReason;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BanReason reason;


    @Column(length=50)
    private String reportedReason;


    @Builder
    public ReportReason(Report report, BanReason reason,String reportedReason) {
        this.report = report;
        this.reason = reason;
        this.reportedReason=reportedReason;
    }
}