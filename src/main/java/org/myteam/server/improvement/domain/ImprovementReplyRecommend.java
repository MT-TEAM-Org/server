//package org.myteam.server.improvement.domain;
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
//public class ImprovementReplyRecommend extends BaseTime {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "improvement_reply_id")
//    private ImprovementReply improvementReply;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "public_id")
//    private Member member;
//
//    @Builder
//    public ImprovementReplyRecommend(ImprovementReply improvementReply, Member member) {
//        this.improvementReply = improvementReply;
//        this.member = member;
//    }
//
//    public static ImprovementReplyRecommend createImprovementReplyRecommend(ImprovementReply improvementReply, Member member) {
//        return ImprovementReplyRecommend.builder()
//                .improvementReply(improvementReply)
//                .member(member)
//                .build();
//    }
//}
