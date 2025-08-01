package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class AdminMemberChangeLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member admin;
    private UUID memberId;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @Builder
    public AdminMemberChangeLog(MemberStatus memberStatus,Member admin, UUID memberId) {
        this.memberStatus = memberStatus;
        this.admin = admin;
        this.memberId = memberId;
    }
}
