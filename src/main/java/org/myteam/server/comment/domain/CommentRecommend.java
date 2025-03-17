package org.myteam.server.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    @Builder
    public CommentRecommend(Comment comment, Member member) {
        this.comment = comment;
        this.member = member;
    }

    public static CommentRecommend createCommentRecommend(Comment boardComment, Member member) {
        return CommentRecommend.builder()
                .comment(boardComment)
                .member(member)
                .build();
    }
}
