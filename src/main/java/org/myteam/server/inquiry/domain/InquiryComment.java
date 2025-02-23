package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InquiryComment extends BaseTime {

    private static final int COUNT_SETTING_NUMBER = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    private String imageUrl;

    private String comment;

    private String createdIp;

    @Column(nullable = false)
    private int recommendCount;

    @Builder
    public InquiryComment(Inquiry inquiry, Member member, String imageUrl, String comment, String createdIp,
                        int recommendCount) {
        this.inquiry = inquiry;
        this.member = member;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.createdIp = createdIp;
        this.recommendCount = recommendCount;
    }

    public static InquiryComment createComment(Inquiry inquiry, Member member, String imageUrl, String comment,
                                                  String createdIp) {
        final int COUNT_SETTING_NUMBER = 0;
        return InquiryComment.builder()
                .inquiry(inquiry)
                .member(member)
                .imageUrl(imageUrl)
                .comment(comment)
                .createdIp(createdIp)
                .recommendCount(COUNT_SETTING_NUMBER)
                .build();
    }

    public void updateComment(String imageUrl, String comment) {
        this.imageUrl = imageUrl;
        this.comment = comment;
    }

    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
     */
    public void verifyInquiryCommentAuthor(Member member) {
        if (!this.isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }
}
