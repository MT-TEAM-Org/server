package org.myteam.server.admin.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;

import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor
public class AdminChangeLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member admin;
    private UUID publicId;
    private Long contentId;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    @Enumerated(EnumType.STRING)
    private StaticDataType staticDataType;
    @Enumerated(EnumType.STRING)
    private AdminControlType adminControlType;

    @Builder
    public AdminChangeLog(MemberStatus memberStatus, StaticDataType staticDataType
            , AdminControlType adminControlType, Member admin, UUID publicId, Long contentId) {
        this.memberStatus = memberStatus;
        this.staticDataType = staticDataType;
        this.adminControlType = adminControlType;
        this.admin = admin;
        this.publicId = publicId;
        this.contentId = contentId;
    }
}
