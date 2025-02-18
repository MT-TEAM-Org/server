package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryCount {

    @Id
    private Long inquiryId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "Inquiry_id")
    private Inquiry inquiry;

    @Column(nullable = false)
    private int recommendCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int commentCount;

    @Builder
    public InquiryCount(Inquiry inquiry, int recommendCount, int commentCount, int viewCount) {
        this.inquiry = inquiry;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
    }

    public static InquiryCount createCount(Inquiry inquiry) {
        final int COUNT_SETTING_NUMBER = 0;

        return InquiryCount.builder()
                .inquiry(inquiry)
                .recommendCount(COUNT_SETTING_NUMBER)
                .commentCount(COUNT_SETTING_NUMBER)
                .viewCount(COUNT_SETTING_NUMBER)
                .build();
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

    public void minusCommentCount(int count) {
        this.commentCount -= count;
    }

    public void minusCommentCount() {
        this.commentCount -= 1;
    }
}
