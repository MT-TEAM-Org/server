package org.myteam.server.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private int visitCount = 0;

    @Column(nullable = false)
    private int inviteCount = 0;

    private String latestIp;

    private LocalDateTime latestAccessTime;

    public MemberActivity(Member member) {
        this.member = member;
        member.updateMemberActivity(this);
    }

    public void increaseVisitCount() {
        this.visitCount += 1;
    }

    public void increaseInviteCount() {
        this.inviteCount += 1;
    }

    public void updateLatestIp(String ip) {
        this.latestIp = ip;
    }

    public void updateLatestAccessTime(LocalDateTime now) {
        this.latestAccessTime = now;
    }

}