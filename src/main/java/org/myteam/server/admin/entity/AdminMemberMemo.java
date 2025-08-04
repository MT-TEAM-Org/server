package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class AdminMemberMemo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member writer;
    private UUID memberId;
    @Builder
    public AdminMemberMemo(String content, Member writer, UUID memberId) {
        this.content = content;
        this.writer = writer;
        this.memberId = memberId;
    }
}
