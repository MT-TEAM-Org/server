package org.myteam.server.comment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.domain.Notice;

@Entity
//@DiscriminatorValue("NOTICE")  // 테이블의 comment_type 값이 "BOARD"이면 이 클래스 사용
@NoArgsConstructor
public class NoticeComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder
    public NoticeComment(Notice notice, Member member, Member mentionedMember,  String comment, String imageUrl, String createdIp, Comment parent) {
        super(member, mentionedMember, comment, imageUrl, createdIp, parent, CommentType.NOTICE);
        this.notice = notice;
    }

    public static NoticeComment createComment(Notice notice, Member member, Member mentionedMember,
                                              String comment, String imageUrl, String createdIp, Comment parent) {
        return NoticeComment.builder()
                .notice(notice)
                .member(member)
                .mentionedMember(mentionedMember)
                .comment(comment)
                .imageUrl(imageUrl)
                .createdIp(createdIp)
                .parent(parent)
                .build();
    }
}

