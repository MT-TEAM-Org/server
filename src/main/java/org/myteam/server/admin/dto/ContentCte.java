package org.myteam.server.admin.dto;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.member.domain.MemberStatus;

import java.time.LocalDateTime;

@CTE
@Getter
@Entity
public class ContentCte {

    @Id
    private Long contentId;
    private String name;
    @Enumerated(EnumType.STRING)
    private StaticDataType staticDataType;
    private String content;
    private LocalDateTime createAt;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    @Enumerated(EnumType.STRING)
    private AdminControlType adminControlType;
}
