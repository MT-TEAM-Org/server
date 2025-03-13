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
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImprovementReply extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "improvement_comment_id")
    private ImprovementComment improvementComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    private String imageUrl;

    private String comment;

    private String createdIp;

    @Column(nullable = false)
    private int recommendCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_public_id")
    private Member mentionedMember; // 대댓글 내용에 언급된 댓글 작성자

    @Builder
    public ImprovementReply(ImprovementComment improvementComment, Member member, String imageUrl,
                            String comment, String createdIp, int recommendCount, Member mentionedMember) {
        this.improvementComment = improvementComment;
        this.member = member;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.createdIp = createdIp;
        this.recommendCount = recommendCount;
        this.mentionedMember = mentionedMember;
    }

    public static ImprovementReply createImprovementReply(ImprovementComment improvementComment, Member member, String imageUrl,
                                                          String comment, String createdIp, Member mentionedMember) {
        final int COUNT_SETTING_NUMBER = 0;
        return ImprovementReply.builder()
                .improvementComment(improvementComment)
                .member(member)
                .imageUrl(imageUrl)
                .comment(comment)
                .createdIp(createdIp)
                .recommendCount(COUNT_SETTING_NUMBER)
                .mentionedMember(mentionedMember)
                .build();
    }

    public void updateReply(String imageUrl, String comment, Member mentionedMember) {
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.mentionedMember = mentionedMember;
    }

    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
     */
    public void verifyNoticeReplyAuthor(Member member) {
        if (!isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }

    public void addRecommendCount() {
        this.recommendCount += 1;
    }


    public void minusRecommendCount() {
        this.recommendCount -= 1;
    }
}
