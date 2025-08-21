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
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_block",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}))
public class MemberBlock extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blocker_id", nullable = false)
    private UUID blocker;

    @Column(name = "blocked_id", nullable = false)
    private UUID blocked;


    @Builder
    private MemberBlock(UUID blocker, UUID blocked, LocalDateTime bannedAt) {
        this.blocker = blocker;
        this.blocked = blocked;
    }

    /**
     * 차단 엔티티 생성
     */
    public static MemberBlock createMemberBlock(UUID blocker,UUID blocked) {
        return MemberBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .bannedAt(LocalDateTime.now())
                .build();
    }

}
