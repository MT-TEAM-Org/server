package org.myteam.server.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_activity")
@Getter
@NoArgsConstructor
public class MemberActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "visit_count", nullable = false)
    private int visitCount = 0;

    @Column(name = "invite_count", nullable = false)
    private int inviteCount = 0;

    public void increaseVisitCount() {
        this.visitCount += 1;
    }

    public void increaseInviteCount() {
        this.inviteCount += 1;
    }

    public MemberActivity(Member member) {
        this.member = member;
        member.setMemberActivity(this);
    }
}