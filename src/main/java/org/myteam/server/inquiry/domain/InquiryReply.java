//package org.myteam.server.inquiry.domain;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.myteam.server.board.domain.BoardComment;
//import org.myteam.server.board.domain.BoardReply;
//import org.myteam.server.global.domain.BaseTime;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.member.entity.Member;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class InquiryReply extends BaseTime {
//
//    private static final int COUNT_SETTING_NUMBER = 0;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "inquiry_comment_id")
//    private InquiryComment inquiryComment;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "public_id")
//    private Member member;
//
//    private String imageUrl;
//
//    private String comment;
//
//    private String createdIp;
//
//    @Column(nullable = false)
//    private int recommendCount;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "mentioned_public_id")
//    private Member mentionedMember;
//
//    @Builder
//    public InquiryReply(InquiryComment inquiryComment, Member member, String imageUrl, String comment, String createdIp,
//                      int recommendCount, Member mentionedMember) {
//        this.inquiryComment = inquiryComment;
//        this.member = member;
//        this.imageUrl = imageUrl;
//        this.comment = comment;
//        this.createdIp = createdIp;
//        this.recommendCount = recommendCount;
//        this.mentionedMember = mentionedMember;
//    }
//
//    public static InquiryReply createInquiryReply(InquiryComment inquiryComment, Member member, String imageUrl, String comment,
//                                              String createdIp, Member mentionedMember) {
//        return InquiryReply.builder()
//                .inquiryComment(inquiryComment)
//                .member(member)
//                .imageUrl(imageUrl)
//                .comment(comment)
//                .createdIp(createdIp)
//                .recommendCount(COUNT_SETTING_NUMBER)
//                .mentionedMember(mentionedMember)
//                .build();
//    }
//
//    public void updateReply(String imageUrl, String comment, Member mentionedMember) {
//        this.imageUrl = imageUrl;
//        this.comment = comment;
//        this.mentionedMember = mentionedMember;
//    }
//
//    public boolean isAuthor(Member member) {
//        return this.member.equals(member);
//    }
//
//    /**
//     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
//     */
//    public void verifyBoardReplyAuthor(Member member) {
//        if (!this.isAuthor(member) && !member.isAdmin()) {
//            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
//        }
//    }
//}
