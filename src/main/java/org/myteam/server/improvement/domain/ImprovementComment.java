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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ImprovementComment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "improvement_id")
    private Improvement improvement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    private String imageUrl;

    private String comment;

    private String createdIp;

    @Column(nullable = false)
    private int recommendCount;

    @Builder
    public ImprovementComment(Improvement improvement, Member member, String imageUrl,
                              String comment, String createdIp, int recommendCount) {
        this.improvement = improvement;
        this.member = member;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.createdIp = createdIp;
        this.recommendCount = recommendCount;
    }

    public static ImprovementComment createImprovementComment(Improvement improvement, Member member, String imageUrl,
                                                              String comment, String createdIp) {
        final int COUNT_SETTING_NUMBER = 0;
        return ImprovementComment.builder()
                .improvement(improvement)
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

    public void addRecommendCount() {
        this.recommendCount += 1;
    }


    public void minusRecommendCount() {
        this.recommendCount -= 1;
    }

    public void verifyNoticeCommentAuthor(Member member) {
        if (!isAuthor(this.member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }
}
