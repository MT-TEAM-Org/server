package org.myteam.server.comment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.member.entity.Member;

@Entity
@DiscriminatorValue("IMPROVEMENT")
@NoArgsConstructor
public class ImprovementComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "improvement_id")
    private Improvement improvement;

    @Builder
    public ImprovementComment(Member member, Member mentionedMember, String comment, String imageUrl, String createdIp, Comment parent, Improvement improvement) {
        super(member, mentionedMember, comment, imageUrl, createdIp, parent, CommentType.IMPROVEMENT);
        this.improvement = improvement;
    }

    public static ImprovementComment createComment(Improvement improvement, Member member, Member mentionedMember,
                                                   String comment, String imageUrl, String createdIp, Comment parent) {
        return ImprovementComment.builder()
                .improvement(improvement)
                .member(member)
                .mentionedMember(mentionedMember)
                .comment(comment)
                .imageUrl(imageUrl)
                .createdIp(createdIp)
                .parent(parent)
                .build();
    }
}
