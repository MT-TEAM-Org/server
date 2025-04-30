package org.myteam.server.comment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.member.entity.Member;

@Entity
@NoArgsConstructor
public class MatchComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Builder
    public MatchComment(Member member, Member mentionedMember, String comment, String imageUrl, String createdIp,
                        Comment parent, Match match) {
        super(member, mentionedMember, comment, imageUrl, createdIp, parent, CommentType.MATCH);
        this.match = match;
    }

    public static MatchComment createComment(Match match, Member member, Member mentionedMember,
                                             String comment, String imageUrl, String createdIp, Comment parent) {
        return MatchComment.builder()
                .match(match)
                .member(member)
                .mentionedMember(mentionedMember)
                .comment(comment)
                .imageUrl(imageUrl)
                .createdIp(createdIp)
                .parent(parent)
                .build();
    }
}