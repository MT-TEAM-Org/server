package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class AdminMemo extends BaseTime {



    @Id
    @GeneratedValue
    private Long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;
    private UUID memberId;
    @Enumerated(EnumType.STRING)
    private StaticDataType staticDataType;
    private Long contentId;



    @Builder
    public AdminMemo(String content, Member writer, UUID memberId, StaticDataType staticDataType, Long contentId) {
        this.content = content;
        this.writer = writer;
        this.memberId = memberId;
        this.staticDataType = staticDataType;
        this.contentId = contentId;
    }
}
