package org.myteam.server.inquiry.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.member.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryRecommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public InquiryRecommend(Long id, Inquiry inquiry, Member member) {
        this.id = id;
        this.inquiry = inquiry;
        this.member = member;
    }
}
