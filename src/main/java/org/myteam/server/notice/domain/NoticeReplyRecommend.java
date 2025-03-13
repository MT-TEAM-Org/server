//package org.myteam.server.notice.domain;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.myteam.server.global.domain.BaseTime;
//import org.myteam.server.member.entity.Member;
//
//@Getter
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class NoticeReplyRecommend extends BaseTime {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "notice_reply_id")
//    private NoticeReply noticeReply;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "public_id")
//    private Member member;
//
//    @Builder
//    public NoticeReplyRecommend(NoticeReply noticeReply, Member member) {
//        this.noticeReply = noticeReply;
//        this.member = member;
//    }
//
//    public static NoticeReplyRecommend createNoticeReplyRecommend(NoticeReply noticeReply, Member member) {
//        return NoticeReplyRecommend.builder()
//                .noticeReply(noticeReply)
//                .member(member)
//                .build();
//    }
//}
