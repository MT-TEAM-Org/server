package org.myteam.server.admin.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.Base;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminResponseMemo extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private UUID writerId;
    @Column(nullable = false)
    private Long inquiryId;
    private String content;


    @Builder
    public AdminResponseMemo(UUID id,Long inquiryId,String content){
        this.content=content;
        this.inquiryId=inquiryId;
        this.writerId=id;

    }
}
