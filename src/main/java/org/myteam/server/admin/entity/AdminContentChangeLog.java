package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
public class AdminContentChangeLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_id")
    private Member admin;
    private Long contentId;
    @Enumerated(EnumType.STRING)
    private StaticDataType staticDataType;
    @Enumerated(EnumType.STRING)
    private AdminControlType adminControlType;

    @Builder
    public AdminContentChangeLog(StaticDataType staticDataType
            , AdminControlType adminControlType, Member admin,Long contentId) {
        this.staticDataType = staticDataType;
        this.adminControlType = adminControlType;
        this.admin = admin;
        this.contentId = contentId;
    }

}
