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
//public class ImprovementCommentRecommend extends BaseTime {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "improvement_comment_id")
//    private ImprovementComment improvementComment;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "public_id")
//    private Member member;
//
//    @Builder
//    public ImprovementCommentRecommend(ImprovementComment improvementComment, Member member) {
//        this.improvementComment = improvementComment;
//        this.member = member;
//    }
//
//    public static ImprovementCommentRecommend createImprovementCommentRecommend(ImprovementComment improvementComment,
//                                                                                Member member) {
//        return ImprovementCommentRecommend.builder()
//                .improvementComment(improvementComment)
//                .member(member)
//                .build();
//    }
//}
