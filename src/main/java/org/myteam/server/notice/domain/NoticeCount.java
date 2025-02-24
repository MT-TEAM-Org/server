package org.myteam.server.notice.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Column(nullable = false)
    private int recommendCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int commentCount;

    @Builder
    public NoticeCount(Notice notice, int recommendCount, int commentCount, int viewCount) {
        this.notice = notice;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
    }

    public static NoticeCount createNoticeCount(Notice notice) {
        final int COUNT_SETTING_NUMBER = 0;

        return NoticeCount.builder()
                .notice(notice)
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
