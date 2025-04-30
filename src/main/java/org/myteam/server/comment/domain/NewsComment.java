package org.myteam.server.comment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;

@Entity
//@DiscriminatorValue("NEWS")
@NoArgsConstructor
public class NewsComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private News news;

    @Builder
    public NewsComment(Member member, Member mentionedMember, String comment, String imageUrl, String createdIp, Comment parent, News news) {
        super(member, mentionedMember, comment, imageUrl, createdIp, parent, CommentType.NEWS);
        this.news = news;
    }

    public static NewsComment createComment(News news, Member member, Member mentionedMember,
                                            String comment, String imageUrl, String createdIp, Comment parent) {
        return NewsComment.builder()
                .news(news)
                .member(member)
                .mentionedMember(mentionedMember)
                .comment(comment)
                .imageUrl(imageUrl)
                .createdIp(createdIp)
                .parent(parent)
                .build();
    }
}
