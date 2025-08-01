package org.myteam.server.admin.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
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
