package org.myteam.server.comment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;

@Entity
@DiscriminatorValue("INQUIRY")
@NoArgsConstructor
public class InquiryComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @Builder
    public InquiryComment(Inquiry inquiry, Member mentionedMember, Member member, String imageUrl, String comment, String createdIp, Comment parent) {
        super(member, mentionedMember, comment, imageUrl, createdIp, parent, CommentType.INQUIRY);
        this.inquiry = inquiry;
    }

    public static InquiryComment createComment(Inquiry inquiry, Member member, Member mentionedMember,
                                               String comment, String imageUrl, String createdIp, Comment parent) {
        return InquiryComment.builder()
                .inquiry(inquiry)
                .member(member)
                .mentionedMember(mentionedMember)
                .comment(comment)
                .imageUrl(imageUrl)
                .createdIp(createdIp)
                .parent(parent)
                .build();
    }
}
