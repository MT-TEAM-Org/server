package org.myteam.server.improvement.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImprovementCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "improvement_id")
    private Improvement improvement;

    @Column(nullable = false)
    private int recommendCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int commentCount;

    @Builder
    public ImprovementCount(Improvement improvement, int recommendCount, int viewCount, int commentCount) {
        this.improvement = improvement;
        this.recommendCount = recommendCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }

    public static ImprovementCount createImprovementCount(Improvement improvement) {
        final int COUNT_SETTING_NUMBER = 0;

        return ImprovementCount.builder()
                .improvement(improvement)
                .recommendCount(COUNT_SETTING_NUMBER)
                .commentCount(COUNT_SETTING_NUMBER)
                .viewCount(COUNT_SETTING_NUMBER)
                .build();
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public void addRecommendCount() {
        this.recommendCount += 1;
    }

    public void minusRecommendCount() {
        this.recommendCount -= 1;
    }

    public void addCommentCount() {
        this.commentCount += 1;
    }

    /**
     * 댓글 1 감소
     */
    public void minusCommentCount() {
        this.commentCount -= 1;
    }

    /**
     * 댓글 count 만큼 감소
     */
    public void minusCommentCount(int count) {
        this.commentCount -= count;
    }
}
