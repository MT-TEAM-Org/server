package org.myteam.server.improvement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;

import static org.myteam.server.improvement.domain.ImprovementStatus.PENDING;
import static org.myteam.server.improvement.domain.ImprovementStatus.RECEIVED;
import static org.myteam.server.improvement.domain.ImprovementStatus.COMPLETED;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Improvement extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    private String title;

    private String content;

    private String createdIp;

    private String imgUrl;

    @Enumerated(EnumType.STRING)
    private ImprovementStatus improvementStatus = PENDING;

    @OneToOne(mappedBy = "improvement", cascade = CascadeType.ALL, orphanRemoval = true)
    private ImprovementCount improvementCount;

    @Enumerated(EnumType.STRING)
    private ImportantStatus importantStatus=ImportantStatus.NORMAL;

    private String link;

    @Builder
    public Improvement(Member member, String title, String content, String createdIp,
                       String imgUrl, ImprovementCount improvementCount, String link) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.createdIp = createdIp;
        this.imgUrl = imgUrl;
        this.improvementCount = improvementCount;
        this.link = link;
    }

    public void updateImprovement(String title, String content, String imgUrl, String link) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.link = link;
    }

    public void updateState(ImprovementStatus status) {
        if (status == null) {
            this.improvementStatus = this.improvementStatus.nextStatus();
        } else {
            this.improvementStatus = status;
        }
    }

    public void updateImportantStatus(ImportantStatus importantStatus){
        this.importantStatus=importantStatus;
    }
}
