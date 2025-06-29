package org.myteam.server.admin.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.BaseTime;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMemo extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false,updatable = false)
    private  UUID reportedId;

    private String content;

    @Builder
    public MemberMemo(UUID uuid,String content){
        this.reportedId=uuid;
        this.content=content;

    }


    public void updateContent(String content){
        this.content=content;
    }

}
