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

    private String createdIP;

    private String imgUrl;

    @Enumerated(EnumType.STRING)
    private ImprovementStatus improvementStatus = PENDING;

    @OneToOne(mappedBy = "improvement", cascade = CascadeType.ALL, orphanRemoval = true)
    private ImprovementCount improvementCount;

    @Builder
    public Improvement(Member member, String title, String content, String createdIP, String imgUrl, ImprovementCount improvementCount) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.createdIP = createdIP;
        this.imgUrl = imgUrl;
        this.improvementCount = improvementCount;
    }

    public void updateImprovement(String title, String content, String imgUrl) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public void updateState() {
        this.improvementStatus = this.improvementStatus.nextStatus();
    }
}
