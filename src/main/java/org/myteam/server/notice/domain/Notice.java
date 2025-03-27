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
public class Notice extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member member;

    private String title;

    private String content;

    private String createdIp;

    private String imgUrl;

    @OneToOne(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private NoticeCount noticeCount;
    private String link;

    @Builder
    public Notice(Member member, String title, String content,
                  String createdIp, String imgUrl, NoticeCount noticeCount, String link) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.createdIp = createdIp;
        this.imgUrl = imgUrl;
        this.noticeCount = noticeCount;
        this.link = link;
    }

    // TODO: update 하면 request 넣기
    public void updateNotice(String title, String content, String imgUrl, String link) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.link = link;
    }
}
