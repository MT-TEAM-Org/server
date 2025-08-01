package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
public class AdminInquiryChangeLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member admin;
    private Long contentId;
    private boolean isAnswered;
    private boolean isMember;
    @Builder
    public AdminInquiryChangeLog(boolean isAnswered,boolean isMember
            , Member admin, Long contentId) {
        this.isMember=isMember;
        this.isAnswered=isAnswered;
        this.admin = admin;
        this.contentId = contentId;
    }
}
