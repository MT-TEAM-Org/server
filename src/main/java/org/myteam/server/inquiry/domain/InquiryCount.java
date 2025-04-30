package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Inquiry_id")
    private Inquiry inquiry;

    @Column(nullable = false)
    private int recommendCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int commentCount;

    @Builder
    public InquiryCount(Inquiry inquiry, int commentCount) {
        this.inquiry = inquiry;
        this.commentCount = commentCount;
        this.viewCount = 0;
        this.recommendCount = 0;
    }

    public static InquiryCount createCount(Inquiry inquiry) {
        final int COUNT_SETTING_NUMBER = 0;

        return InquiryCount.builder()
                .inquiry(inquiry)
                .commentCount(COUNT_SETTING_NUMBER)
                .build();
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
