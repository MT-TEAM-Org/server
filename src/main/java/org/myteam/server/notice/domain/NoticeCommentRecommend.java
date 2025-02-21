package org.myteam.server.notice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeCommentRecommend extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_comment_id")
    private NoticeComment noticeComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    @Builder
    public NoticeCommentRecommend(NoticeComment noticeComment, Member member) {
        this.noticeComment = noticeComment;
        this.member = member;
    }

    public static NoticeCommentRecommend createNoticeCommentRecommend(NoticeComment noticeComment, Member member) {
        return NoticeCommentRecommend.builder()
                .noticeComment(noticeComment)
                .member(member)
                .build();
    }
}
