package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.entity.Member;
@Entity
@Getter
@NoArgsConstructor
public class AdminImproveChangeLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member admin;
    private Long contentId;
    @Enumerated(EnumType.STRING)
    private ImprovementStatus improvementStatus;
    @Enumerated(EnumType.STRING)
    private ImportantStatus importantStatus;

    @Builder
    public AdminImproveChangeLog(
            ImportantStatus importantStatus,ImprovementStatus improvementStatus
            , Member admin,Long contentId) {
        this.importantStatus=importantStatus;
        this.improvementStatus=improvementStatus;
        this.admin = admin;
        this.contentId = contentId;
    }
}
