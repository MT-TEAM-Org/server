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

    private String createdIP;

    private String imgUrl;

    @OneToOne(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private NoticeCount noticeCount;

    @Builder
    public Notice(Member member, String title, String content,
                  String createdIP, String imgUrl, NoticeCount noticeCount) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.createdIP = createdIP;
        this.imgUrl = imgUrl;
        this.noticeCount = noticeCount;
    }

    // TODO: update 하면 request 넣기
    public void updateNotice() {

    }
}
