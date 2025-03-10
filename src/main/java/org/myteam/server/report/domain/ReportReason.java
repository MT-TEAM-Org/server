package org.myteam.server.report.domain;

import jakarta.persistence.*;
import lombok.*;
import org.myteam.server.chat.domain.BanReason;

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

    @Builder
    public ReportReason(Report report, BanReason reason) {
        this.report = report;
        this.reason = reason;
    }
}