package org.myteam.server.comment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // 단일 테이블 전략 사용
@DiscriminatorColumn(name = "d_type") // 타입 구분 컬럼 추가
public abstract class Comment extends BaseTime {

    private static final int MAX_DEPTH = 3; // 최대 대댓글 깊이 제한

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_public_id")
    private Member mentionedMember; // 대댓글 내용에 언급된 댓글 작성자

    private String comment;
    private String imageUrl;
    private String createdIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false)
    private int recommendCount = 0;

    @Column(nullable = false)
    private int depth = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentType commentType;

    public Comment(Member member, Member mentionedMember, String comment,
                   String imageUrl, String createdIp, Comment parent, CommentType type) {
        this.member = member;
        this.mentionedMember = mentionedMember;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.createdIp = createdIp;
        this.parent = parent;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
        this.commentType = type;
    }

    public void updateComment(String imageUrl, String comment, Member mentionedMember) {
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.mentionedMember = mentionedMember;
    }

    // 작성자 true
    public boolean isAuthor(Member member) {
        return this.member.getPublicId().equals(member.getPublicId());
    }

    // 작성자 + 관리자면 true
    public boolean verifyCommentAuthor(Member member) {
        return isAuthor(member) || member.isAdmin();
    }

    // 추천 + 1
    public void addRecommendCount() {
        this.recommendCount += 1;
    }

    // 추천 하나 삭제
    public void minusRecommendCount() {
        this.recommendCount -= 1;
    }

    // 최대 깊이 제한
    public boolean canAddReply() {
        return this.depth < MAX_DEPTH;
    }
}

