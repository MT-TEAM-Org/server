package org.myteam.server.chat.block.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_block",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}))
public class MemberBlock extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private Member blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<BanReason> reasons = new ArrayList<>(); // 밴 사유

    private String message;

    @Builder
    private MemberBlock(Member blocker, Member blocked, List<BanReason> reasons, LocalDateTime bannedAt, String message) {
        this.blocker = blocker;
        this.blocked = blocked;
        if (reasons != null) {
            this.reasons.addAll(reasons);
        }
        this.message = message;
    }

    /**
     * 차단 엔티티 생성
     */
    public static MemberBlock createMemberBlock(Member blocker, Member blocked, List<BanReason> reasons, String message) {
        return MemberBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .reasons(reasons)
                .bannedAt(LocalDateTime.now())
                .message(message)
                .build();
    }

}
